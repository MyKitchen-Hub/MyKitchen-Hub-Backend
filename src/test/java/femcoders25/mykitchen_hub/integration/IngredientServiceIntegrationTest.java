package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientUpdateDto;
import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.ingredient.repository.IngredientRepository;
import femcoders25.mykitchen_hub.ingredient.service.IngredientService;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IngredientServiceIntegrationTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockitoBean
    private femcoders25.mykitchen_hub.email.EmailService emailService;

    private User testUser;
    private Recipe testRecipe;
    private IngredientDto testIngredientDto;

    @BeforeEach
    void setUp() {
        ingredientRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();

        UserRegistrationDto userDto = new UserRegistrationDto(
                "testuser",
                "test@example.com",
                "password123");
        testUser = userService.createUser(userDto);

        testRecipe = new Recipe();
        testRecipe.setTitle("Test Recipe");
        testRecipe.setDescription("Test Description");
        testRecipe.setTag("italian");
        testRecipe.setCreatedBy(testUser);
        testRecipe = recipeRepository.save(testRecipe);

        testIngredientDto = new IngredientDto("Tomato", 2.0, "pieces");
    }

    @Test
    void createIngredient_ValidData_CreatesIngredientSuccessfully() {
        IngredientResponseDto result = ingredientService.createIngredient(testIngredientDto, testRecipe);

        assertNotNull(result.id());
        assertEquals("Tomato", result.name());
        assertEquals(2.0, result.amount());
        assertEquals("pieces", result.unit());
        assertEquals(testRecipe.getId(), result.recipeId());

        Ingredient savedIngredient = ingredientRepository.findById(result.id()).orElse(null);
        assertNotNull(savedIngredient);
        assertEquals("Tomato", savedIngredient.getName());
        assertEquals(2.0, savedIngredient.getAmount());
        assertEquals("pieces", savedIngredient.getUnit());
        assertEquals(testRecipe.getId(), savedIngredient.getRecipe().getId());
    }

    @Test
    void getIngredientById_ExistingIngredient_ReturnsIngredient() {
        IngredientResponseDto created = ingredientService.createIngredient(testIngredientDto, testRecipe);

        IngredientResponseDto result = ingredientService.getIngredientById(created.id());

        assertNotNull(result);
        assertEquals(created.id(), result.id());
        assertEquals("Tomato", result.name());
        assertEquals(2.0, result.amount());
        assertEquals("pieces", result.unit());
        assertEquals(testRecipe.getId(), result.recipeId());
    }

    @Test
    void getIngredientById_NonExistingIngredient_ThrowsResourceNotFoundException() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.getIngredientById(999L));

        assertEquals("Ingredient not found with id: 999", exception.getMessage());
    }

    @Test
    void updateIngredient_ExistingIngredient_UpdatesIngredientSuccessfully() {
        IngredientResponseDto created = ingredientService.createIngredient(testIngredientDto, testRecipe);
        IngredientUpdateDto updateDto = new IngredientUpdateDto("Updated Tomato", 3.0, "kg");

        IngredientResponseDto result = ingredientService.updateIngredient(created.id(), updateDto);

        assertNotNull(result);
        assertEquals(created.id(), result.id());
        assertEquals("Updated Tomato", result.name());
        assertEquals(3.0, result.amount());
        assertEquals("kg", result.unit());
        assertEquals(testRecipe.getId(), result.recipeId());

        Ingredient updatedIngredient = ingredientRepository.findById(created.id()).orElse(null);
        assertNotNull(updatedIngredient);
        assertEquals("Updated Tomato", updatedIngredient.getName());
        assertEquals(3.0, updatedIngredient.getAmount());
        assertEquals("kg", updatedIngredient.getUnit());
    }

    @Test
    void updateIngredient_NonExistingIngredient_ThrowsResourceNotFoundException() {
        IngredientUpdateDto updateDto = new IngredientUpdateDto("Updated Tomato", 3.0, "kg");

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.updateIngredient(999L, updateDto));

        assertEquals("Ingredient not found with id: 999", exception.getMessage());
    }

    @Test
    void deleteIngredient_ExistingIngredient_DeletesIngredientSuccessfully() {
        IngredientResponseDto created = ingredientService.createIngredient(testIngredientDto, testRecipe);
        Long ingredientId = created.id();

        assertTrue(ingredientRepository.existsById(ingredientId));

        ingredientService.deleteIngredient(ingredientId);

        assertFalse(ingredientRepository.existsById(ingredientId));
    }

    @Test
    void deleteIngredient_NonExistingIngredient_ThrowsResourceNotFoundException() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.deleteIngredient(999L));

        assertEquals("Ingredient not found with id: 999", exception.getMessage());
    }

    @Test
    void getIngredientsByRecipeId_ExistingRecipe_ReturnsIngredients() {
        IngredientDto ingredient1 = new IngredientDto("Tomato", 2.0, "pieces");
        IngredientDto ingredient2 = new IngredientDto("Onion", 1.0, "piece");

        ingredientService.createIngredient(ingredient1, testRecipe);
        ingredientService.createIngredient(ingredient2, testRecipe);

        List<IngredientResponseDto> result = ingredientService.getIngredientsByRecipeId(testRecipe.getId());

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(i -> "Tomato".equals(i.name())));
        assertTrue(result.stream().anyMatch(i -> "Onion".equals(i.name())));
    }

    @Test
    void getIngredientsByRecipeId_NonExistingRecipe_ReturnsEmptyList() {
        List<IngredientResponseDto> result = ingredientService.getIngredientsByRecipeId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteIngredientsByRecipeId_ExistingRecipe_DeletesAllIngredients() {
        IngredientDto ingredient1 = new IngredientDto("Tomato", 2.0, "pieces");
        IngredientDto ingredient2 = new IngredientDto("Onion", 1.0, "piece");

        ingredientService.createIngredient(ingredient1, testRecipe);
        ingredientService.createIngredient(ingredient2, testRecipe);

        assertEquals(2, ingredientRepository.findByRecipeId(testRecipe.getId()).size());

        ingredientService.deleteIngredientsByRecipeId(testRecipe.getId());

        assertTrue(ingredientRepository.findByRecipeId(testRecipe.getId()).isEmpty());
    }

    @Test
    void createIngredientsFromDto_ValidList_CreatesAllIngredients() {
        List<IngredientDto> ingredientDtos = Arrays.asList(
                new IngredientDto("Tomato", 2.0, "pieces"),
                new IngredientDto("Onion", 1.0, "piece"),
                new IngredientDto("Garlic", 3.0, "cloves"));

        List<Ingredient> result = ingredientService.createIngredientsFromDto(ingredientDtos, testRecipe);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("Tomato", result.get(0).getName());
        assertEquals("Onion", result.get(1).getName());
        assertEquals("Garlic", result.get(2).getName());

        result.forEach(ingredient -> {
            assertEquals(testRecipe.getId(), ingredient.getRecipe().getId());
            assertNotNull(ingredient.getId());
        });

        List<Ingredient> savedIngredients = ingredientRepository.findByRecipeId(testRecipe.getId());
        assertEquals(3, savedIngredients.size());
    }

    @Test
    void createIngredientsFromDto_EmptyList_ReturnsEmptyList() {
        List<IngredientDto> emptyList = Arrays.asList();

        List<Ingredient> result = ingredientService.createIngredientsFromDto(emptyList, testRecipe);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        assertTrue(ingredientRepository.findByRecipeId(testRecipe.getId()).isEmpty());
    }

    @Test
    void updateIngredient_PartialUpdate_UpdatesOnlyProvidedFields() {
        IngredientResponseDto created = ingredientService.createIngredient(testIngredientDto, testRecipe);
        IngredientUpdateDto partialUpdate = new IngredientUpdateDto("Updated Tomato", null, null);

        IngredientResponseDto result = ingredientService.updateIngredient(created.id(), partialUpdate);

        assertNotNull(result);
        assertEquals("Updated Tomato", result.name());
        assertEquals(2.0, result.amount());
        assertEquals("pieces", result.unit());

        Ingredient updatedIngredient = ingredientRepository.findById(created.id()).orElse(null);
        assertNotNull(updatedIngredient);
        assertEquals("Updated Tomato", updatedIngredient.getName());
        assertEquals(2.0, updatedIngredient.getAmount());
        assertEquals("pieces", updatedIngredient.getUnit());
    }

    @Test
    void ingredientLifecycle_CreateUpdateDelete_WorksCorrectly() {
        IngredientResponseDto created = ingredientService.createIngredient(testIngredientDto, testRecipe);
        assertNotNull(created.id());
        assertEquals("Tomato", created.name());

        IngredientUpdateDto updateDto = new IngredientUpdateDto("Ripe Tomato", 4.0, "pieces");
        IngredientResponseDto updated = ingredientService.updateIngredient(created.id(), updateDto);
        assertEquals("Ripe Tomato", updated.name());
        assertEquals(4.0, updated.amount());

        IngredientResponseDto retrieved = ingredientService.getIngredientById(created.id());
        assertEquals("Ripe Tomato", retrieved.name());
        assertEquals(4.0, retrieved.amount());

        ingredientService.deleteIngredient(created.id());

        assertThrows(ResourceNotFoundException.class,
                () -> ingredientService.getIngredientById(created.id()));
    }
}
