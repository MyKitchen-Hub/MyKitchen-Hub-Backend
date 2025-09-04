package femcoders25.mykitchen_hub.ingredient.repository;

import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByRecipe(Recipe recipe);

    List<Ingredient> findByRecipeId(Long recipeId);

    @Query("SELECT i FROM Ingredient i WHERE i.recipe.id = :recipeId")
    List<Ingredient> findIngredientsByRecipeId(@Param("recipeId") Long recipeId);

    void deleteByRecipe(Recipe recipe);

    void deleteByRecipeId(Long recipeId);
}
