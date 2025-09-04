package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.recipe.dto.RecipeCreateDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeResponseDto;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.recipe.service.RecipeService;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RecipeServiceIntegrationTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private RecipeCreateDto testRecipeCreate;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        recipeRepository.deleteAll();

        UserRegistrationDto userDto = new UserRegistrationDto(
                "testuser",
                "test@example.com",
                "password123");
        userService.createUser(userDto);

        testRecipeCreate = new RecipeCreateDto(
                "Test Recipe",
                "Test Description",
                List.of(),
                null,
                "italian");
    }

    @Test
    @WithMockUser(username = "testuser")
    void createRecipe_ValidData_CreatesRecipeSuccessfully() {
        RecipeResponseDto result = recipeService.createRecipe(testRecipeCreate);

        assertNotNull(result.id());
        assertEquals("Test Recipe", result.title());
        assertEquals("Test Description", result.description());
        assertEquals("italian", result.tag());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllRecipes_ReturnsPagedRecipes() {
        recipeService.createRecipe(testRecipeCreate);
        RecipeCreateDto anotherRecipe = new RecipeCreateDto(
                "Another Recipe",
                "Another Description",
                List.of(),
                null,
                "chinese");
        recipeService.createRecipe(anotherRecipe);

        Page<femcoders25.mykitchen_hub.recipe.dto.RecipeListDto> result = recipeService
                .getAllRecipes(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecipeById_ExistingRecipe_ReturnsRecipe() {
        RecipeResponseDto createdRecipe = recipeService.createRecipe(testRecipeCreate);

        RecipeResponseDto result = recipeService.getRecipeById(createdRecipe.id());

        assertNotNull(result);
        assertEquals(createdRecipe.id(), result.id());
        assertEquals("Test Recipe", result.title());
    }

    @Test
    @WithMockUser(username = "testuser")
    void searchRecipesByIngredient_ExistingIngredient_ReturnsRecipes() {
        recipeService.createRecipe(testRecipeCreate);

        Page<RecipeResponseDto> result = recipeService.searchRecipesByIngredient(
                "tomato", PageRequest.of(0, 10));

        assertEquals(0, result.getTotalElements());
    }

    @Test
    @WithMockUser(username = "testuser")
    void searchRecipesByTag_ExistingTag_ReturnsRecipes() {
        recipeService.createRecipe(testRecipeCreate);

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTag(
                "italian", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("italian", result.getContent().get(0).tag());
    }

    @Test
    @WithMockUser(username = "testuser")
    void searchRecipesByTitle_ExistingTitle_ReturnsRecipes() {
        recipeService.createRecipe(testRecipeCreate);

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTitle(
                "Test", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Recipe", result.getContent().get(0).title());
    }
}
