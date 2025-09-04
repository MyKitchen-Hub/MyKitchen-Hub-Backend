package femcoders25.mykitchen_hub.recipe.dto;

import femcoders25.mykitchen_hub.comment.dto.CommentMapper;
import femcoders25.mykitchen_hub.comment.dto.CommentResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientMapper;
import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;
import femcoders25.mykitchen_hub.like.service.LikeService;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.user.entity.User;

import java.util.List;

public class RecipeMapper {

    public static RecipeResponseDto toRecipeResponseDto(Recipe recipe, LikeService likeService, Long currentUserId) {
        if (recipe == null) {
            return null;
        }

        List<IngredientDto> ingredientDtos = recipe.getIngredients().stream()
                .map(IngredientMapper::toIngredientDto)
                .toList();

        List<CommentResponseDto> commentDtos = recipe.getComments().stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList();

        LikeStatsDto likeStats = likeService.getLikeStats(currentUserId, recipe.getId());

        return new RecipeResponseDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                ingredientDtos,
                recipe.getImageUrl(),
                recipe.getTag(),
                commentDtos,
                likeStats,
                recipe.getCreatedAt(),
                recipe.getUpdatedAt(),
                recipe.getCreatedBy() != null ? recipe.getCreatedBy().getId() : null,
                recipe.getCreatedBy() != null ? recipe.getCreatedBy().getUsername() : null);
    }

    public static RecipeListDto toRecipeListDto(Recipe recipe, LikeService likeService) {
        if (recipe == null) {
            return null;
        }

        long likesCount = likeService.getLikesCount(recipe.getId());
        long dislikesCount = likeService.getDislikesCount(recipe.getId());

        return new RecipeListDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getImageUrl(),
                recipe.getTag(),
                likesCount,
                dislikesCount,
                recipe.getCreatedAt(),
                recipe.getUpdatedAt());
    }

    public static Recipe toRecipe(RecipeCreateDto dto, User createdBy) {
        if (dto == null) {
            return null;
        }

        Recipe recipe = new Recipe();
        recipe.setTitle(dto.title());
        recipe.setDescription(dto.description());
        recipe.setImageUrl(dto.imageUrl());
        recipe.setTag(dto.tag());
        recipe.setCreatedBy(createdBy);

        if (dto.ingredients() != null) {
            List<Ingredient> ingredients = dto.ingredients().stream()
                    .map(ingredientDto -> IngredientMapper.toIngredient(ingredientDto, recipe))
                    .toList();

            ingredients.forEach(recipe::addIngredient);
        }

        return recipe;
    }

    public static void updateRecipeFromDto(Recipe recipe, RecipeUpdateDto dto) {
        if (recipe == null || dto == null) {
            return;
        }

        if (dto.title() != null) {
            recipe.setTitle(dto.title());
        }
        if (dto.description() != null) {
            recipe.setDescription(dto.description());
        }
        if (dto.imageUrl() != null) {
            recipe.setImageUrl(dto.imageUrl());
        }
        if (dto.tag() != null) {
            recipe.setTag(dto.tag());
        }
        if (dto.ingredients() != null) {
            recipe.getIngredients().clear();
            dto.ingredients().forEach(ingredientDto -> {
                Ingredient ingredient = IngredientMapper.toIngredient(ingredientDto, recipe);
                recipe.addIngredient(ingredient);
            });
        }
    }

}
