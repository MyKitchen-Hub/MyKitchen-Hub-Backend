package femcoders25.mykitchen_hub.recipe.dto;

import java.time.LocalDateTime;

public record RecipeListDto(
        Long id,
        String title,
        String description,
        String imageUrl,
        String tag,
        long likesCount,
        long dislikesCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
