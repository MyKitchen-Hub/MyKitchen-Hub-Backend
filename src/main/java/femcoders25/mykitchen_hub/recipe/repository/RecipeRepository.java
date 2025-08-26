package femcoders25.mykitchen_hub.recipe.repository;

import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
