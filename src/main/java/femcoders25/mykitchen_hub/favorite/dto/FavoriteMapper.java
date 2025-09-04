package femcoders25.mykitchen_hub.favorite.dto;

import femcoders25.mykitchen_hub.favorite.entity.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteResponseDto toResponse(Favorite favorite) {
        return new FavoriteResponseDto(
                favorite.getId(),
                favorite.getUser().getId(),
                favorite.getRecipe().getId(),
                favorite.getRecipe().getTitle(),
                favorite.getCreatedAt());
    }
}
