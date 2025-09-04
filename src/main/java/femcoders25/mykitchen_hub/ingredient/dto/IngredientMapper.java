package femcoders25.mykitchen_hub.ingredient.dto;

import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;

public class IngredientMapper {

    public static IngredientDto toIngredientDto(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        return new IngredientDto(
                ingredient.getName(),
                ingredient.getAmount(),
                ingredient.getUnit());
    }

    public static Ingredient toIngredient(IngredientDto dto) {
        if (dto == null) {
            return null;
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setName(dto.name());
        ingredient.setAmount(dto.amount());
        ingredient.setUnit(dto.unit());
        return ingredient;
    }

    public static IngredientResponseDto toIngredientResponseDto(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        return new IngredientResponseDto(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getAmount(),
                ingredient.getUnit(),
                ingredient.getRecipe() != null ? ingredient.getRecipe().getId() : null
        );
    }

    public static Ingredient toIngredient(IngredientDto dto, Recipe recipe) {
        if (dto == null) {
            return null;
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setName(dto.name());
        ingredient.setAmount(dto.amount());
        ingredient.setUnit(dto.unit());
        ingredient.setRecipe(recipe);
        return ingredient;
    }

    public static void updateIngredientFromDto(Ingredient ingredient, IngredientUpdateDto dto) {
        if (ingredient == null || dto == null) {
            return;
        }

        if (dto.name() != null) {
            ingredient.setName(dto.name());
        }
        if (dto.amount() != null) {
            ingredient.setAmount(dto.amount());
        }
        if (dto.unit() != null) {
            ingredient.setUnit(dto.unit());
        }
    }
}
