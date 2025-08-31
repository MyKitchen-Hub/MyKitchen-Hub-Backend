package femcoders25.mykitchen_hub.shoppinglist.service;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListCreateDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListMapper;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListResponseDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListUpdateDto;
import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import femcoders25.mykitchen_hub.shoppinglist.entity.MergedIngredient;
import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.shoppinglist.repository.ListItemRepository;
import femcoders25.mykitchen_hub.shoppinglist.repository.ShoppingListRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ListItemRepository listItemRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListMapper shoppingListMapper;

    public ShoppingListResponseDto createShoppingList(ShoppingListCreateDto createDto, User user) {
        log.info("Creating shopping list for user: {}", user.getUsername());

        List<Recipe> recipes = recipeRepository.findAllById(createDto.recipeIds());
        if (recipes.size() != createDto.recipeIds().size()) {
            throw new ResourceNotFoundException("Some recipes not found");
        }

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(createDto.name());
        shoppingList.setGeneratedBy(user);
        shoppingList.setGeneratedFromRecipe(recipes.stream()
                .map(Recipe::getTitle)
                .collect(Collectors.joining(", ")));

        List<ListItem> listItems = generateMergedIngredients(recipes);
        ShoppingList savedShoppingList = shoppingListRepository.save(shoppingList);
        listItems.forEach(item -> item.setShoppingList(savedShoppingList));
        listItemRepository.saveAll(listItems);
        savedShoppingList.setListItems(listItems);

        log.info("Shopping list created successfully with {} items", listItems.size());
        return shoppingListMapper.toResponseDto(savedShoppingList);
    }

    private List<ListItem> generateMergedIngredients(List<Recipe> recipes) {
        Map<String, MergedIngredient> mergedIngredients = new HashMap<>();

        for (Recipe recipe : recipes) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                String key = ingredient.getName().toLowerCase() + "|" + ingredient.getUnit().toLowerCase();

                MergedIngredient merged = mergedIngredients.get(key);
                if (merged == null) {
                    merged = new MergedIngredient(ingredient.getName(), ingredient.getUnit(), 0.0);
                    mergedIngredients.put(key, merged);
                }
                merged.addAmount(ingredient.getAmount());
            }
        }

        return mergedIngredients.values().stream()
                .map(this::createListItem)
                .collect(Collectors.toList());
    }

    private ListItem createListItem(MergedIngredient merged) {
        ListItem item = new ListItem();
        item.setName(merged.getName());
        item.setAmount(merged.getTotalAmount());
        item.setUnit(merged.getUnit());
        item.setIsChecked(false);
        return item;
    }

    public ShoppingListResponseDto getShoppingListById(Long id, User user) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (!shoppingList.getGeneratedBy().getId().equals(user.getId())) {
            throw new UnauthorizedOperationException("Access denied to this shopping list");
        }

        return shoppingListMapper.toResponseDto(shoppingList);
    }

    public List<ShoppingListResponseDto> getUserShoppingLists(User user) {
        List<ShoppingList> shoppingLists = shoppingListRepository.findByGeneratedByOrderByCreatedAtDesc(user);
        return shoppingListMapper.toResponseDtoList(shoppingLists);
    }

    public List<ShoppingListResponseDto> searchUserShoppingLists(User user, String name) {
        List<ShoppingList> shoppingLists = shoppingListRepository
                .findByGeneratedByAndNameContainingIgnoreCaseOrderByCreatedAtDesc(user, name);
        return shoppingListMapper.toResponseDtoList(shoppingLists);
    }

    public ShoppingListResponseDto updateShoppingList(Long id, ShoppingListUpdateDto updateDto, User user) {
        log.info("Updating shopping list with id: {} for user: {}", id, user.getUsername());

        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (!shoppingList.getGeneratedBy().getId().equals(user.getId())) {
            throw new UnauthorizedOperationException("Access denied to this shopping list");
        }

        shoppingList.setName(updateDto.name());

        if (updateDto.recipeIds() != null && !updateDto.recipeIds().isEmpty()) {
            log.info("New recipe IDs provided for shopping list: {}. Recalculating ingredients.", id);

            List<Recipe> newRecipes = recipeRepository.findAllById(updateDto.recipeIds());
            if (newRecipes.size() != updateDto.recipeIds().size()) {
                throw new ResourceNotFoundException("Some recipes not found");
            }

            shoppingList.setGeneratedFromRecipe(newRecipes.stream()
                    .map(Recipe::getTitle)
                    .collect(Collectors.joining(", ")));

            if (shoppingList.getListItems() != null) {
                listItemRepository.deleteAll(shoppingList.getListItems());
                shoppingList.getListItems().clear();
            }

            List<ListItem> newListItems = generateMergedIngredients(newRecipes);
            newListItems.forEach(item -> item.setShoppingList(shoppingList));
            listItemRepository.saveAll(newListItems);

            if (shoppingList.getListItems() == null) {
                shoppingList.setListItems(new ArrayList<>());
            }
            shoppingList.getListItems().addAll(newListItems);

            log.info("Shopping list ingredients recalculated. New items count: {}", newListItems.size());
        }

        ShoppingList updatedShoppingList = shoppingListRepository.save(shoppingList);

        log.info("Shopping list updated successfully. Name: {}, Recipes updated: {}",
                updateDto.name(), updateDto.recipeIds() != null);

        return shoppingListMapper.toResponseDto(updatedShoppingList);
    }

    public ApiResponse deleteShoppingList(Long id, User user) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (!shoppingList.getGeneratedBy().getId().equals(user.getId())) {
            throw new UnauthorizedOperationException("Access denied to this shopping list");
        }

        shoppingListRepository.delete(shoppingList);
        return ApiResponse.success("Shopping list deleted successfully");
    }

    public ApiResponse toggleItemChecked(Long listId, Long itemId, User user) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (!shoppingList.getGeneratedBy().getId().equals(user.getId())) {
            throw new UnauthorizedOperationException("Access denied to this shopping list");
        }

        ListItem listItem = listItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("List item not found"));

        if (!listItem.getShoppingList().getId().equals(listId)) {
            throw new ResourceNotFoundException("Item does not belong to this shopping list");
        }

        listItem.setIsChecked(!Boolean.TRUE.equals(listItem.getIsChecked()));
        listItemRepository.save(listItem);

        return ApiResponse.success("Item status updated successfully");
    }

}

