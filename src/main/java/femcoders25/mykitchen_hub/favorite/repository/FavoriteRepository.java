package femcoders25.mykitchen_hub.favorite.repository;

import femcoders25.mykitchen_hub.favorite.entity.Favorite;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndRecipe(User user, Recipe recipe);

    void deleteByUserAndRecipe(User user, Recipe recipe);

    Page<Favorite> findByUser(User user, Pageable pageable);
}
