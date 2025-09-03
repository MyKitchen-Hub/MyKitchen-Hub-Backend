package femcoders25.mykitchen_hub.recipe.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import femcoders25.mykitchen_hub.cloudinary.CloudinaryService;
import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeCreateDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeListDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeResponseDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeUpdateDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeMapper;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final UserService userService;
    private final RecipeRepository recipeRepository;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;

    @Transactional
    public RecipeResponseDto createRecipe(RecipeCreateDto createDto) {
        return createRecipe(createDto, null);
    }

    @Transactional
    public RecipeResponseDto createRecipe(String title, String description, String ingredientsJson, MultipartFile image,
            String tag) throws IOException {

        List<IngredientDto> ingredients = parseIngredientsJson(ingredientsJson);
        RecipeCreateDto recipeCreateDto = new RecipeCreateDto(title, description, ingredients, null, tag);

        return createRecipe(recipeCreateDto, image);
    }

    @Transactional
    public RecipeResponseDto createRecipe(RecipeCreateDto createDto, MultipartFile image) {
        User currentUser = userService.getCurrentUser();

        Recipe recipe = RecipeMapper.toRecipe(createDto, currentUser);

        String imageUrl = cloudinaryService.uploadImageSafely(image);
        recipe.setImageUrl(imageUrl);

        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Created recipe: {} by user: {} with image: {}",
                savedRecipe.getTitle(), currentUser.getUsername(), imageUrl);

        return RecipeMapper.toRecipeResponseDto(savedRecipe);
    }

    private List<IngredientDto> parseIngredientsJson(String ingredientsJson) {
        if (ingredientsJson == null || ingredientsJson.trim().isEmpty()) {
            return List.of();
        }

        try {
            List<IngredientDto> ingredients = objectMapper.readValue(ingredientsJson,
                    new TypeReference<>() {
                    });

            for (int i = 0; i < ingredients.size(); i++) {
                IngredientDto ingredient = ingredients.get(i);
                if (ingredient.name() == null || ingredient.name().trim().isEmpty()) {
                    throw new IllegalArgumentException("Ingredient at index " + i + " must have a name");
                }
                if (ingredient.amount() == null || ingredient.amount() <= 0) {
                    throw new IllegalArgumentException("Ingredient at index " + i + " must have a positive amount");
                }
                if (ingredient.unit() == null || ingredient.unit().trim().isEmpty()) {
                    throw new IllegalArgumentException("Ingredient at index " + i + " must have a unit");
                }
            }

            return ingredients;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Invalid JSON format for ingredients. Expected format: [{\"name\":\"Flour\",\"amount\":200,\"unit\":\"g\"},{\"name\":\"Sugar\",\"amount\":100,\"unit\":\"g\"}]. Please ensure the JSON is complete and properly formatted. Error: "
                            + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ingredients data: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<RecipeListDto> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(RecipeMapper::toRecipeListDto);
    }

    @Transactional(readOnly = true)
    public RecipeResponseDto getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));
        return RecipeMapper.toRecipeResponseDto(recipe);
    }

    @Transactional
    public RecipeResponseDto updateRecipe(Long id, RecipeUpdateDto updateDto) {
        return updateRecipe(id, updateDto, null);
    }

    @Transactional
    public RecipeResponseDto updateRecipe(Long id, RecipeUpdateDto updateDto, MultipartFile image) {
        log.info("Updating recipe with id: {}", id);
        Recipe recipe = findRecipeAndCheckOwnership(id);
        RecipeMapper.updateRecipeFromDto(recipe, updateDto);

        if (image != null && !image.isEmpty()) {
            String newImageUrl = cloudinaryService.replaceImageSafely(recipe.getImageUrl(), image);
            recipe.setImageUrl(newImageUrl);
            log.info("Image updated for recipe: {} to: {}", recipe.getTitle(), newImageUrl);
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        log.info("Updated recipe: {} by user: {}", updatedRecipe.getTitle(),
                userService.getCurrentUser().getUsername());

        return RecipeMapper.toRecipeResponseDto(updatedRecipe);
    }

    @Transactional
    public RecipeResponseDto updateRecipe(Long id, String title, String description, String ingredientsJson,
            MultipartFile image, String tag) {
        log.info("Updating recipe with id: {} from multipart data", id);

        List<IngredientDto> ingredients = null;
        if (ingredientsJson != null && !ingredientsJson.trim().isEmpty()) {
            ingredients = parseIngredientsJson(ingredientsJson);
        }

        RecipeUpdateDto updateDto = new RecipeUpdateDto(title, description, ingredients, null, tag);
        return updateRecipe(id, updateDto, image);
    }

    private Recipe findRecipeAndCheckOwnership(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));

        User currentUser = userService.getCurrentUser();
        if (!recipe.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("update", "recipe");
        }

        return recipe;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        log.info("Deleting recipe with id: {}", id);
        Recipe recipe = findRecipeAndCheckOwnership(id);

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().equals(cloudinaryService.getDefaultImageUrl())) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(recipe.getImageUrl());
                if (publicId != null) {
                    cloudinaryService.deleteFile(publicId);
                    log.info("Deleted image for recipe: {}", recipe.getTitle());
                }
            } catch (Exception e) {
                log.error("Failed to delete image for recipe: {}, error: {}", recipe.getTitle(), e.getMessage());
            }
        }

        recipeRepository.delete(recipe);
        log.info("Deleted recipe: {} by user: {}", recipe.getTitle(),
                userService.getCurrentUser().getUsername());
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
