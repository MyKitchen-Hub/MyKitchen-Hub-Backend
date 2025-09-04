package femcoders25.mykitchen_hub.ingredient.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IngredientUpdateDto(
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,
        @Positive(message = "Amount must be positive")
        Double amount,
        @Size(min = 1, max = 20, message = "Unit must be between 1 and 20 characters")
        String unit
) {}
