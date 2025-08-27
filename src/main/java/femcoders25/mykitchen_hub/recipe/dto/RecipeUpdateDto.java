package femcoders25.mykitchen_hub.recipe.dto;

import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeUpdateDto(
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
        @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description,
        List<IngredientDto> ingredients,
        String imageUrl,
        String tag) {
}
