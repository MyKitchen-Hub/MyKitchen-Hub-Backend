package femcoders25.mykitchen_hub.recipe.repository;

import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Recipe> findByTagContainingIgnoreCase(String tag, Pageable pageable);

    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingredients i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    Page<Recipe> findByIngredientsNameContainingIgnoreCase(@Param("ingredient") String ingredient, Pageable pageable);

}
