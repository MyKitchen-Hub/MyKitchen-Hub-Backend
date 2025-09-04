package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecipeRepository recipeRepository;

    private User testUser;
    private Recipe testRecipe;
    private Ingredient testIngredient;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        entityManager.persistAndFlush(testUser);

        testRecipe = new Recipe();
        testRecipe.setTitle("Test Recipe");
        testRecipe.setDescription("Test Description");
        testRecipe.setTag("italian");
        testRecipe.setCreatedBy(testUser);
        entityManager.persistAndFlush(testRecipe);

        testIngredient = new Ingredient();
        testIngredient.setName("tomato");
        testIngredient.setAmount(2.0);
        testIngredient.setUnit("pieces");
        testIngredient.setRecipe(testRecipe);
        entityManager.persistAndFlush(testIngredient);
    }

    @Test
    void findByTitleContainingIgnoreCase_ExistingTitle_ReturnsRecipe() {
        Page<Recipe> result = recipeRepository.findByTitleContainingIgnoreCase("test", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Recipe", result.getContent().get(0).getTitle());
    }

    @Test
    void findByTitleContainingIgnoreCase_NonExistingTitle_ReturnsEmpty() {
        Page<Recipe> result = recipeRepository.findByTitleContainingIgnoreCase("nonexistent", PageRequest.of(0, 10));
        
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void findByTagContainingIgnoreCase_ExistingTag_ReturnsRecipe() {
        Page<Recipe> result = recipeRepository.findByTagContainingIgnoreCase("italian", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("italian", result.getContent().get(0).getTag());
    }

    @Test
    void findByIngredientsNameContainingIgnoreCase_ExistingIngredient_ReturnsRecipe() {
        Page<Recipe> result = recipeRepository.findByIngredientsNameContainingIgnoreCase("tomato", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Recipe", result.getContent().get(0).getTitle());
    }

    @Test
    void save_NewRecipe_SavesSuccessfully() {
        Recipe newRecipe = new Recipe();
        newRecipe.setTitle("New Recipe");
        newRecipe.setDescription("New Description");
        newRecipe.setTag("chinese");
        newRecipe.setCreatedBy(testUser);
        
        Recipe saved = recipeRepository.save(newRecipe);
        
        assertNotNull(saved.getId());
        assertEquals("New Recipe", saved.getTitle());
        assertEquals("New Description", saved.getDescription());
    }
}
