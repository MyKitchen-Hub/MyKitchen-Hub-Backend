package femcoders25.mykitchen_hub.recipe.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
import femcoders25.mykitchen_hub.recipe.dto.*;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final UserService userService;
    private final RecipeRepository recipeRepository;

    @Transactional
    public RecipeResponseDto createRecipe(RecipeCreateDto createDto) {
        User currentUser = userService.getCurrentUser();

        Recipe recipe = RecipeMapper.toRecipe(createDto, currentUser);
        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Created recipe: {} by user: {}", savedRecipe.getTitle(), currentUser.getUsername());
        return RecipeMapper.toRecipeResponseDto(savedRecipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponseDto> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(RecipeMapper::toRecipeResponseDto);
    }

    @Transactional(readOnly = true)
    public RecipeResponseDto getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));
        return RecipeMapper.toRecipeResponseDto(recipe);
    }

    @Transactional
    public RecipeResponseDto updateRecipe(Long id, RecipeUpdateDto updateDto) {
        log.info("Updating recipe with id: {}", id);
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));

        User currentUser = userService.getCurrentUser();
        if (!recipe.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("update", "recipe");
        }
        RecipeMapper.updateRecipeFromDto(recipe, updateDto);

        log.info("Recipe updated, saving to database...");
        Recipe updatedRecipe = recipeRepository.save(recipe);
        log.info("Updated recipe: {} by user: {}", updatedRecipe.getTitle(), currentUser.getUsername());
        return RecipeMapper.toRecipeResponseDto(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        log.info("Deleting recipe with id: {}", id);
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));
        User currentUser = userService.getCurrentUser();
        if (!recipe.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("delete", "recipe");
        }
        recipeRepository.delete(recipe);
        log.info("Deleted recipe: {} by user: {}", recipe.getTitle(), currentUser.getUsername());
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponseDto> searchRecipesByTitle(String title, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title, pageable);

        if (recipes.isEmpty()) {
            log.info("No recipes found with title containing: '{}'", title);
        } else {
            log.info("Found {} recipes with title containing: '{}'", recipes.getTotalElements(), title);
        }

        return recipes.map(RecipeMapper::toRecipeResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponseDto> searchRecipesByIngredient(String ingredient, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByIngredientsNameContainingIgnoreCase(ingredient, pageable);

        if (recipes.isEmpty()) {
            log.info("No recipes found with ingredient containing: '{}'", ingredient);
        } else {
            log.info("Found {} recipes with ingredient containing: '{}'", recipes.getTotalElements(), ingredient);
        }

        return recipes.map(RecipeMapper::toRecipeResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponseDto> searchRecipesByTag(String tag, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByTagContainingIgnoreCase(tag, pageable);

        if (recipes.isEmpty()) {
            log.info("No recipes found with tag containing: '{}'", tag);
        } else {
            log.info("Found {} recipes with tag containing: '{}'", recipes.getTotalElements(), tag);
        }

        return recipes.map(RecipeMapper::toRecipeResponseDto);
    }
}
