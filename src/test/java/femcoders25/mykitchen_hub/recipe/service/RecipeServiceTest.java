package femcoders25.mykitchen_hub.recipe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import femcoders25.mykitchen_hub.cloudinary.CloudinaryService;
import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;
import femcoders25.mykitchen_hub.like.service.LikeService;
import femcoders25.mykitchen_hub.recipe.dto.*;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private LikeService likeService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecipeService recipeService;

    private User user;
    private Recipe recipe;
    private RecipeCreateDto createDto;
    private RecipeUpdateDto updateDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");
        recipe.setCreatedBy(user);

        createDto = new RecipeCreateDto("Test Recipe", "Test Description", List.of(), null, "Test Tag");
        updateDto = new RecipeUpdateDto("Updated Recipe", "Updated Description", List.of(), null, "Updated Tag");
        pageable = PageRequest.of(0, 10);

        LikeStatsDto likeStats = new LikeStatsDto(0L, 0L, false, false);
        lenient().when(likeService.getLikeStats(any(Long.class), any(Long.class))).thenReturn(likeStats);
        lenient().when(likeService.getLikesCount(any(Long.class))).thenReturn(0L);
        lenient().when(likeService.getDislikesCount(any(Long.class))).thenReturn(0L);
    }

    @Test
    void testCreateRecipe() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.uploadImageSafely(null)).thenReturn("http://localhost:8080/images/logo.png");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.createRecipe(createDto);

        assertNotNull(result);
        verify(userService).getCurrentUser();
        verify(cloudinaryService).uploadImageSafely(null);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testDeleteRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(recipeRepository).delete(recipe);

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(recipeRepository).findById(1L);
        verify(userService, times(2)).getCurrentUser();
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void testDeleteRecipeWithImage() {
        Recipe recipeWithImage = new Recipe();
        recipeWithImage.setId(1L);
        recipeWithImage.setTitle("Test Recipe");
        recipeWithImage.setImageUrl("https://res.cloudinary.com/test/image/upload/v123456789/recipe_image.jpg");
        recipeWithImage.setCreatedBy(user);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipeWithImage));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.getDefaultImageUrl()).thenReturn("http://localhost:8080/images/logo.png");
        when(cloudinaryService
                .extractPublicIdFromUrl("https://res.cloudinary.com/test/image/upload/v123456789/recipe_image.jpg"))
                .thenReturn("recipe_image");
        doNothing().when(recipeRepository).delete(recipeWithImage);

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(recipeRepository).findById(1L);
        verify(userService, times(2)).getCurrentUser();
        verify(cloudinaryService).getDefaultImageUrl();
        verify(cloudinaryService)
                .extractPublicIdFromUrl("https://res.cloudinary.com/test/image/upload/v123456789/recipe_image.jpg");
        verify(recipeRepository).delete(recipeWithImage);
    }

    @Test
    void testDeleteRecipe_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(2L);
        recipe.setCreatedBy(otherUser);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(UnauthorizedOperationException.class, () -> recipeService.deleteRecipe(1L));
        verify(recipeRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(recipeRepository, never()).delete(any());
    }

    @Test
    void testGetAllRecipes() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findAll(pageable)).thenReturn(recipePage);

        Page<RecipeListDto> result = recipeService.getAllRecipes(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findAll(pageable);
    }

    @Test
    void testGetRecipeById() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        RecipeResponseDto result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        verify(recipeRepository).findById(1L);
    }

    @Test
    void testGetRecipeById_NotFound() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(1L));
        verify(recipeRepository).findById(1L);
    }

    @Test
    void testSearchRecipesByIngredient() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByIngredientsNameContainingIgnoreCase("tomato", pageable)).thenReturn(recipePage);

        Page<RecipeResponseDto> result = recipeService.searchRecipesByIngredient("tomato", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByIngredientsNameContainingIgnoreCase("tomato", pageable);
    }

    @Test
    void testSearchRecipesByTag() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByTagContainingIgnoreCase("italian", pageable)).thenReturn(recipePage);

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTag("italian", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByTagContainingIgnoreCase("italian", pageable);
    }

    @Test
    void testSearchRecipesByTitle() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByTitleContainingIgnoreCase("pasta", pageable)).thenReturn(recipePage);

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTitle("pasta", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByTitleContainingIgnoreCase("pasta", pageable);
    }

    @Test
    void testUpdateRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.updateRecipe(1L, updateDto);

        assertNotNull(result);
        verify(recipeRepository).findById(1L);
        verify(userService, times(2)).getCurrentUser();
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testUpdateRecipe_NotFound() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.updateRecipe(1L, updateDto));
        verify(recipeRepository).findById(1L);
        verify(userService, never()).getCurrentUser();
    }

    @Test
    void testUpdateRecipe_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(2L);
        recipe.setCreatedBy(otherUser);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(UnauthorizedOperationException.class, () -> recipeService.updateRecipe(1L, updateDto));
        verify(recipeRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(recipeRepository, never()).save(any());
    }

    @Test
    void testCreateRecipeWithImage() {
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.uploadImageSafely(image)).thenReturn("https://cloudinary.com/test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.createRecipe(createDto, image);

        assertNotNull(result);
        verify(cloudinaryService).uploadImageSafely(image);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testCreateRecipeWithMultipartData_NullIngredientsJson() throws IOException {
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.uploadImageSafely(image)).thenReturn("https://cloudinary.com/test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.createRecipe("Test Recipe", "Test Description", null, image,
                "Test Tag");

        assertNotNull(result);
        verify(cloudinaryService).uploadImageSafely(image);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testCreateRecipeWithMultipartData_EmptyIngredientsJson() throws IOException {
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.uploadImageSafely(image)).thenReturn("https://cloudinary.com/test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.createRecipe("Test Recipe", "Test Description", "", image, "Test Tag");

        assertNotNull(result);
        verify(cloudinaryService).uploadImageSafely(image);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testCreateRecipeWithMultipartData_InvalidJson() {
        String invalidJson = "invalid json";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        try {
            doThrow(new RuntimeException("Invalid JSON")).when(objectMapper)
                    .readValue(eq(invalidJson), any(com.fasterxml.jackson.core.type.TypeReference.class));
        } catch (Exception e) {
        }

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", invalidJson, image, "Test Tag"));
    }

    @Test
    void testCreateRecipeWithMultipartData_IngredientWithoutName() throws IOException {
        String ingredientsJson = "[{\"name\":\"\",\"amount\":200,\"unit\":\"g\"}]";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        List<IngredientDto> ingredients = List.of(new IngredientDto("", 200.0, "g"));

        doReturn(ingredients).when(objectMapper)
                .readValue(eq(ingredientsJson), any(com.fasterxml.jackson.core.type.TypeReference.class));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", ingredientsJson, image,
                        "Test Tag"));
    }

    @Test
    void testCreateRecipeWithMultipartData_IngredientWithNullName() throws IOException {
        String ingredientsJson = "[{\"name\":null,\"amount\":200,\"unit\":\"g\"}]";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        List<IngredientDto> ingredients = List.of(new IngredientDto(null, 200.0, "g"));

        doReturn(ingredients).when(objectMapper)
                .readValue(eq(ingredientsJson), any(com.fasterxml.jackson.core.type.TypeReference.class));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", ingredientsJson, image,
                        "Test Tag"));
    }

    @Test
    void testCreateRecipeWithMultipartData_IngredientWithInvalidAmount() throws IOException {
        String ingredientsJson = "[{\"name\":\"Flour\",\"amount\":0,\"unit\":\"g\"}]";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        List<IngredientDto> ingredients = List.of(new IngredientDto("Flour", 0.0, "g"));

        doReturn(ingredients).when(objectMapper)
                .readValue(eq(ingredientsJson), any(com.fasterxml.jackson.core.type.TypeReference.class));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", ingredientsJson, image,
                        "Test Tag"));
    }

    @Test
    void testCreateRecipeWithMultipartData_IngredientWithNullAmount() throws IOException {
        String ingredientsJson = "[{\"name\":\"Flour\",\"amount\":null,\"unit\":\"g\"}]";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        List<IngredientDto> ingredients = List.of(new IngredientDto("Flour", null, "g"));

        doReturn(ingredients).when(objectMapper)
                .readValue(eq(ingredientsJson), any(com.fasterxml.jackson.core.type.TypeReference.class));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", ingredientsJson, image,
                        "Test Tag"));
    }

    @Test
    void testCreateRecipeWithMultipartData_IngredientWithEmptyUnit() throws IOException {
        String ingredientsJson = "[{\"name\":\"Flour\",\"amount\":200,\"unit\":\"\"}]";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        List<IngredientDto> ingredients = List.of(new IngredientDto("Flour", 200.0, ""));

        doReturn(ingredients).when(objectMapper)
                .readValue(eq(ingredientsJson), any(com.fasterxml.jackson.core.type.TypeReference.class));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", ingredientsJson, image,
                        "Test Tag"));
    }

    @Test
    void testCreateRecipeWithMultipartData_IngredientWithNullUnit() throws IOException {
        String ingredientsJson = "[{\"name\":\"Flour\",\"amount\":200,\"unit\":null}]";
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        List<IngredientDto> ingredients = List.of(new IngredientDto("Flour", 200.0, null));

        doReturn(ingredients).when(objectMapper)
                .readValue(eq(ingredientsJson), any(com.fasterxml.jackson.core.type.TypeReference.class));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe("Test Recipe", "Test Description", ingredientsJson, image,
                        "Test Tag"));
    }

    @Test
    void testUpdateRecipeWithImage() {
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.replaceImageSafely(any(), any()))
                .thenReturn("https://cloudinary.com/new-test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.updateRecipe(1L, updateDto, image);

        assertNotNull(result);
        verify(cloudinaryService).replaceImageSafely(any(), any());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testUpdateRecipeWithEmptyImage() {
        MultipartFile emptyImage = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.updateRecipe(1L, updateDto, emptyImage);

        assertNotNull(result);
        verify(cloudinaryService, never()).replaceImageSafely(any(), any());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testUpdateRecipeWithMultipartData_NullIngredientsJson() throws IOException {
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.replaceImageSafely(any(), any()))
                .thenReturn("https://cloudinary.com/new-test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.updateRecipe(1L, "Updated Recipe", "Updated Description", null, image,
                "Updated Tag");

        assertNotNull(result);
        verify(cloudinaryService).replaceImageSafely(any(), any());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testUpdateRecipeWithMultipartData_EmptyIngredientsJson() throws IOException {
        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.replaceImageSafely(any(), any()))
                .thenReturn("https://cloudinary.com/new-test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeResponseDto result = recipeService.updateRecipe(1L, "Updated Recipe", "Updated Description", "", image,
                "Updated Tag");

        assertNotNull(result);
        verify(cloudinaryService).replaceImageSafely(any(), any());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void testDeleteRecipeWithDefaultImage() throws IOException {
        recipe.setImageUrl("http://localhost:8080/images/logo.png");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.getDefaultImageUrl()).thenReturn("http://localhost:8080/images/logo.png");
        doNothing().when(recipeRepository).delete(recipe);

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(cloudinaryService, never()).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService, never()).deleteFile(anyString());
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void testDeleteRecipeWithNullImage() throws IOException {
        recipe.setImageUrl(null);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(recipeRepository).delete(recipe);

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(cloudinaryService, never()).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService, never()).deleteFile(anyString());
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void testDeleteRecipeWithImageDeletionFailure() throws IOException {
        recipe.setImageUrl("https://res.cloudinary.com/test/image/upload/v123456789/recipe_image.jpg");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.getDefaultImageUrl()).thenReturn("http://localhost:8080/images/logo.png");
        when(cloudinaryService.extractPublicIdFromUrl(anyString())).thenReturn("recipe_image");
        doThrow(new RuntimeException("Cloudinary error")).when(cloudinaryService).deleteFile("recipe_image");
        doNothing().when(recipeRepository).delete(recipe);

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(cloudinaryService).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService).deleteFile("recipe_image");
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void testDeleteRecipeWithNullPublicId() throws IOException {
        recipe.setImageUrl("https://res.cloudinary.com/test/image/upload/v123456789/recipe_image.jpg");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cloudinaryService.getDefaultImageUrl()).thenReturn("http://localhost:8080/images/logo.png");
        when(cloudinaryService.extractPublicIdFromUrl(anyString())).thenReturn(null);
        doNothing().when(recipeRepository).delete(recipe);

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(cloudinaryService).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService, never()).deleteFile(anyString());
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void testSearchRecipesByTitle_EmptyResults() {
        Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList());
        when(recipeRepository.findByTitleContainingIgnoreCase("nonexistent", pageable)).thenReturn(emptyPage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTitle("nonexistent", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(recipeRepository).findByTitleContainingIgnoreCase("nonexistent", pageable);
    }

    @Test
    void testSearchRecipesByIngredient_EmptyResults() {
        Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList());
        when(recipeRepository.findByIngredientsNameContainingIgnoreCase("nonexistent", pageable)).thenReturn(emptyPage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        Page<RecipeResponseDto> result = recipeService.searchRecipesByIngredient("nonexistent", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(recipeRepository).findByIngredientsNameContainingIgnoreCase("nonexistent", pageable);
    }

    @Test
    void testSearchRecipesByTag_EmptyResults() {
        Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList());
        when(recipeRepository.findByTagContainingIgnoreCase("nonexistent", pageable)).thenReturn(emptyPage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTag("nonexistent", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(recipeRepository).findByTagContainingIgnoreCase("nonexistent", pageable);
    }

    @Test
    void testGetRecipeById_WithCurrentUser() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        RecipeResponseDto result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        verify(recipeRepository).findById(1L);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testGetRecipeById_WithoutCurrentUser() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.empty());

        RecipeResponseDto result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        verify(recipeRepository).findById(1L);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testSearchRecipesByTitle_WithCurrentUser() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByTitleContainingIgnoreCase("pasta", pageable)).thenReturn(recipePage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTitle("pasta", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByTitleContainingIgnoreCase("pasta", pageable);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testSearchRecipesByTitle_WithoutCurrentUser() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByTitleContainingIgnoreCase("pasta", pageable)).thenReturn(recipePage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.empty());

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTitle("pasta", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByTitleContainingIgnoreCase("pasta", pageable);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testSearchRecipesByIngredient_WithCurrentUser() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByIngredientsNameContainingIgnoreCase("tomato", pageable)).thenReturn(recipePage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        Page<RecipeResponseDto> result = recipeService.searchRecipesByIngredient("tomato", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByIngredientsNameContainingIgnoreCase("tomato", pageable);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testSearchRecipesByIngredient_WithoutCurrentUser() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByIngredientsNameContainingIgnoreCase("tomato", pageable)).thenReturn(recipePage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.empty());

        Page<RecipeResponseDto> result = recipeService.searchRecipesByIngredient("tomato", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByIngredientsNameContainingIgnoreCase("tomato", pageable);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testSearchRecipesByTag_WithCurrentUser() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByTagContainingIgnoreCase("italian", pageable)).thenReturn(recipePage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.of(1L));

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTag("italian", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByTagContainingIgnoreCase("italian", pageable);
        verify(userService).getCurrentUserIdOptional();
    }

    @Test
    void testSearchRecipesByTag_WithoutCurrentUser() {
        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findByTagContainingIgnoreCase("italian", pageable)).thenReturn(recipePage);
        when(userService.getCurrentUserIdOptional()).thenReturn(Optional.empty());

        Page<RecipeResponseDto> result = recipeService.searchRecipesByTag("italian", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(recipeRepository).findByTagContainingIgnoreCase("italian", pageable);
        verify(userService).getCurrentUserIdOptional();
    }
}
