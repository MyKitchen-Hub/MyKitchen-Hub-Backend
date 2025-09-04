package femcoders25.mykitchen_hub.like.controller;

import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;
import femcoders25.mykitchen_hub.like.service.LikeService;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LikeController likeController;

    private LikeStatsDto likeStats;

    @BeforeEach
    void setUp() {
        likeStats = new LikeStatsDto(5L, 2L, true, false);
    }

    @Test
    void likeRecipe_ShouldReturnLikeStats() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(likeService.likeRecipe(1L, 1L)).thenReturn(likeStats);

        ResponseEntity<LikeStatsDto> response = likeController.likeRecipe(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(likeStats, response.getBody());
        assertEquals(5L, response.getBody().likesCount());
        assertEquals(2L, response.getBody().dislikesCount());
        assertTrue(response.getBody().userLiked());
        assertFalse(response.getBody().userDisliked());

        verify(userService).getCurrentUserId();
        verify(likeService).likeRecipe(1L, 1L);
    }

    @Test
    void dislikeRecipe_ShouldReturnLikeStats() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(likeService.dislikeRecipe(1L, 1L)).thenReturn(likeStats);

        ResponseEntity<LikeStatsDto> response = likeController.dislikeRecipe(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(likeStats, response.getBody());
        assertEquals(5L, response.getBody().likesCount());
        assertEquals(2L, response.getBody().dislikesCount());
        assertTrue(response.getBody().userLiked());
        assertFalse(response.getBody().userDisliked());

        verify(userService).getCurrentUserId();
        verify(likeService).dislikeRecipe(1L, 1L);
    }

    @Test
    void getRecipeStats_ShouldReturnLikeStats() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(likeService.getLikeStats(1L, 1L)).thenReturn(likeStats);

        ResponseEntity<LikeStatsDto> response = likeController.getRecipeStats(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(likeStats, response.getBody());
        assertEquals(5L, response.getBody().likesCount());
        assertEquals(2L, response.getBody().dislikesCount());
        assertTrue(response.getBody().userLiked());
        assertFalse(response.getBody().userDisliked());

        verify(userService).getCurrentUserId();
        verify(likeService).getLikeStats(1L, 1L);
    }

    @Test
    void likeRecipe_WithDifferentRecipeId_ShouldCallServiceWithCorrectId() {
        when(userService.getCurrentUserId()).thenReturn(2L);
        when(likeService.likeRecipe(2L, 5L)).thenReturn(likeStats);

        ResponseEntity<LikeStatsDto> response = likeController.likeRecipe(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeService).likeRecipe(2L, 5L);
    }

    @Test
    void dislikeRecipe_WithDifferentRecipeId_ShouldCallServiceWithCorrectId() {
        when(userService.getCurrentUserId()).thenReturn(3L);
        when(likeService.dislikeRecipe(3L, 10L)).thenReturn(likeStats);

        ResponseEntity<LikeStatsDto> response = likeController.dislikeRecipe(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeService).dislikeRecipe(3L, 10L);
    }

    @Test
    void getRecipeStats_WithDifferentRecipeId_ShouldCallServiceWithCorrectId() {
        when(userService.getCurrentUserId()).thenReturn(4L);
        when(likeService.getLikeStats(4L, 15L)).thenReturn(likeStats);

        ResponseEntity<LikeStatsDto> response = likeController.getRecipeStats(15L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeService).getLikeStats(4L, 15L);
    }

    @Test
    void likeRecipe_WhenServiceReturnsEmptyStats_ShouldReturnEmptyStats() {
        LikeStatsDto emptyStats = new LikeStatsDto(0L, 0L, false, false);
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(likeService.likeRecipe(1L, 1L)).thenReturn(emptyStats);

        ResponseEntity<LikeStatsDto> response = likeController.likeRecipe(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0L, response.getBody().likesCount());
        assertEquals(0L, response.getBody().dislikesCount());
        assertFalse(response.getBody().userLiked());
        assertFalse(response.getBody().userDisliked());
    }
}
