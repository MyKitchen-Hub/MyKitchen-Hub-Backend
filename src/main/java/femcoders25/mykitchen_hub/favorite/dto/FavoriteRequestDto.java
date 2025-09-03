package femcoders25.mykitchen_hub.favorite.dto;

import jakarta.validation.constraints.NotNull;

public record FavoriteRequestDto(
        @NotNull(message = "Recipe ID is required") Long recipeId) {
}
