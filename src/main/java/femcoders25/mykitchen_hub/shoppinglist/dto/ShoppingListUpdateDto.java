package femcoders25.mykitchen_hub.shoppinglist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ShoppingListUpdateDto(
        @NotBlank(message = "Shopping list name is required") String name,

        @NotEmpty(message = "At least one recipe must be selected") @NotNull(message = "Recipe IDs cannot be null") List<Long> recipeIds) {
}

