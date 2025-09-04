package femcoders25.mykitchen_hub.like.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;
import femcoders25.mykitchen_hub.like.entity.Like;
import femcoders25.mykitchen_hub.like.repository.LikeRepository;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private LikeService likeService;

    private User user;
    private Recipe recipe;
    private Like existingLike;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");
        recipe.setDescription("Test Description");
        recipe.setCreatedBy(user);

        existingLike = new Like();
        existingLike.setId(1L);
        existingLike.setUser(user);
        existingLike.setRecipe(recipe);
        existingLike.setIsLike(true);
        existingLike.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void likeRecipe_WhenNoExistingLike_ShouldCreateNewLike() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.findByUserAndRecipe(user, recipe))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingLike));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(1L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(0L);
        when(likeRepository.save(any(Like.class))).thenReturn(existingLike);

        LikeStatsDto result = likeService.likeRecipe(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.likesCount());
        assertEquals(0L, result.dislikesCount());
        assertTrue(result.userLiked());
        assertFalse(result.userDisliked());

        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    void likeRecipe_WhenExistingLike_ShouldToggleLike() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.findByUserAndRecipe(user, recipe))
                .thenReturn(Optional.of(existingLike))
                .thenReturn(Optional.empty());
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(0L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(0L);

        LikeStatsDto result = likeService.likeRecipe(1L, 1L);

        assertNotNull(result);
        assertEquals(0L, result.likesCount());
        assertEquals(0L, result.dislikesCount());
        assertFalse(result.userLiked());
        assertFalse(result.userDisliked());

        verify(likeRepository).delete(existingLike);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void likeRecipe_WhenExistingDislike_ShouldChangeToLike() {
        existingLike.setIsLike(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(existingLike));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(1L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(0L);
        when(likeRepository.save(existingLike)).thenReturn(existingLike);

        LikeStatsDto result = likeService.likeRecipe(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.likesCount());
        assertEquals(0L, result.dislikesCount());
        assertTrue(result.userLiked());
        assertFalse(result.userDisliked());

        assertTrue(existingLike.getIsLike());
        verify(likeRepository).save(existingLike);
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    void dislikeRecipe_WhenNoExistingLike_ShouldCreateNewDislike() {
        existingLike.setIsLike(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.findByUserAndRecipe(user, recipe))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingLike));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(0L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(1L);
        when(likeRepository.save(any(Like.class))).thenReturn(existingLike);

        LikeStatsDto result = likeService.dislikeRecipe(1L, 1L);

        assertNotNull(result);
        assertEquals(0L, result.likesCount());
        assertEquals(1L, result.dislikesCount());
        assertFalse(result.userLiked());
        assertTrue(result.userDisliked());

        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    void dislikeRecipe_WhenExistingDislike_ShouldToggleDislike() {
        existingLike.setIsLike(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.findByUserAndRecipe(user, recipe))
                .thenReturn(Optional.of(existingLike))
                .thenReturn(Optional.empty());
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(0L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(0L);

        LikeStatsDto result = likeService.dislikeRecipe(1L, 1L);

        assertNotNull(result);
        assertEquals(0L, result.likesCount());
        assertEquals(0L, result.dislikesCount());
        assertFalse(result.userLiked());
        assertFalse(result.userDisliked());

        verify(likeRepository).delete(existingLike);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void dislikeRecipe_WhenExistingLike_ShouldChangeToDislike() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(existingLike));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(0L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(1L);
        when(likeRepository.save(existingLike)).thenReturn(existingLike);

        LikeStatsDto result = likeService.dislikeRecipe(1L, 1L);

        assertNotNull(result);
        assertEquals(0L, result.likesCount());
        assertEquals(1L, result.dislikesCount());
        assertFalse(result.userLiked());
        assertTrue(result.userDisliked());

        assertFalse(existingLike.getIsLike());
        verify(likeRepository).save(existingLike);
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    void getLikeStats_ShouldReturnCorrectStats() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(5L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(2L);
        when(likeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(existingLike));

        LikeStatsDto result = likeService.getLikeStats(1L, 1L);

        assertNotNull(result);
        assertEquals(5L, result.likesCount());
        assertEquals(2L, result.dislikesCount());
        assertTrue(result.userLiked());
        assertFalse(result.userDisliked());
    }

    @Test
    void getLikeStats_WhenUserHasNoLike_ShouldReturnCorrectStats() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(3L);
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(1L);
        when(likeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());

        LikeStatsDto result = likeService.getLikeStats(1L, 1L);

        assertNotNull(result);
        assertEquals(3L, result.likesCount());
        assertEquals(1L, result.dislikesCount());
        assertFalse(result.userLiked());
        assertFalse(result.userDisliked());
    }

    @Test
    void getLikesCount_ShouldReturnCorrectCount() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.countLikesByRecipe(recipe)).thenReturn(10L);

        long result = likeService.getLikesCount(1L);

        assertEquals(10L, result);
    }

    @Test
    void getDislikesCount_ShouldReturnCorrectCount() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(likeRepository.countDislikesByRecipe(recipe)).thenReturn(3L);

        long result = likeService.getDislikesCount(1L);

        assertEquals(3L, result);
    }

    @Test
    void likeRecipe_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> likeService.likeRecipe(1L, 1L));
    }

    @Test
    void likeRecipe_WhenRecipeNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> likeService.likeRecipe(1L, 1L));
    }
}
