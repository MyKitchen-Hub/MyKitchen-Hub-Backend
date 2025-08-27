package femcoders25.mykitchen_hub.recipe.dto;

import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeCreateDto(
        @NotBlank(message = "Title is required") @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
        @NotBlank(message = "Description is required") @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description,
        @NotNull(message = "Ingredients are required")
        List<IngredientDto> ingredients,
        String imageUrl,
        String tag) {}
