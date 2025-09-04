package femcoders25.mykitchen_hub.like.repository;

import femcoders25.mykitchen_hub.like.entity.Like;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndRecipe(User user, Recipe recipe);

    boolean existsByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.recipe = :recipe AND l.isLike = true")
    long countLikesByRecipe(@Param("recipe") Recipe recipe);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.recipe = :recipe AND l.isLike = false")
    long countDislikesByRecipe(@Param("recipe") Recipe recipe);

    void deleteByUserAndRecipe(User user, Recipe recipe);
}
