package femcoders25.mykitchen_hub.favorite.dto;

import java.time.LocalDateTime;

public record FavoriteResponseDto(
        Long id,
        Long userId,
        Long recipeId,
        String recipeTitle,
        LocalDateTime createdAt) {
}
