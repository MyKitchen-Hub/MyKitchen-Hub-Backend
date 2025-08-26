package femcoders25.mykitchen_hub.ingredient.repository;

import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
