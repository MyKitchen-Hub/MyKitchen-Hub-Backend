package femcoders25.mykitchen_hub.recipe.service;

import femcoders25.mykitchen_hub.cloudinary.CloudinaryService;
import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
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
}
