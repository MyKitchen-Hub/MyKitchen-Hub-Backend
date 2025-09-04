package femcoders25.mykitchen_hub.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngredientDto(
        @NotBlank(message = "Ingredient name is required")
        String name,
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive")
        Double amount,
        @NotBlank(message = "Unit is required")
        String unit) {}

