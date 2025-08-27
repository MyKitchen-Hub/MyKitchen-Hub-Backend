package femcoders25.mykitchen_hub.recipe.dto;

import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeResponseDto(
        Long id,
        String title,
        String description,
        List<IngredientDto> ingredients,
        String imageUrl,
        String tag,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdById,
        String createdByUsername) {
}
