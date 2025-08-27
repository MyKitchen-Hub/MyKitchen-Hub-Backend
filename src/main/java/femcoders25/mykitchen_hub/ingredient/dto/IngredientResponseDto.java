package femcoders25.mykitchen_hub.ingredient.dto;

public record IngredientResponseDto(
        Long id,
        String name,
        Double amount,
        String unit,
        Long recipeId
) {}
