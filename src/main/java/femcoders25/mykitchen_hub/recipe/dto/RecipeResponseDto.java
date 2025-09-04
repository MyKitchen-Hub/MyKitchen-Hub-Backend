package femcoders25.mykitchen_hub.recipe.dto;

import femcoders25.mykitchen_hub.comment.dto.CommentResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeResponseDto(
        Long id,
        String title,
        String description,
        List<IngredientDto> ingredients,
        String imageUrl,
        String tag,
        List<CommentResponseDto> comments,
        LikeStatsDto likeStats,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdById,
        String createdByUsername) {
}
