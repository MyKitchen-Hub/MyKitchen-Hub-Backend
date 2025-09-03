package femcoders25.mykitchen_hub.favorite.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteRequestDto;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteResponseDto;
import femcoders25.mykitchen_hub.favorite.service.FavoriteService;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FavoriteController favoriteController;

    @Test
    void addToFavorites_Success() {
        FavoriteRequestDto request = new FavoriteRequestDto(1L);
        FavoriteResponseDto response = new FavoriteResponseDto(1L, 1L, 1L, "Test Recipe", LocalDateTime.now());

        when(userService.getCurrentUserId()).thenReturn(1L);
        when(favoriteService.addToFavorites(anyLong(), any(FavoriteRequestDto.class)))
                .thenReturn(response);

        ResponseEntity<ApiResponse<FavoriteResponseDto>> result = favoriteController.addToFavorites(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Recipe added to favorites successfully", result.getBody().getMessage());
        assertEquals(1L, result.getBody().getData().id());
        assertEquals(1L, result.getBody().getData().userId());
        assertEquals(1L, result.getBody().getData().recipeId());
        assertEquals("Test Recipe", result.getBody().getData().recipeTitle());
    }

    @Test
    void removeFromFavorites_Success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        doNothing().when(favoriteService).removeFromFavorites(1L, 1L);

        ResponseEntity<ApiResponse<String>> result = favoriteController.removeFromFavorites(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Recipe removed from favorites successfully", result.getBody().getMessage());
    }

    @Test
    void getUserFavorites_Success() {
        FavoriteResponseDto response = new FavoriteResponseDto(1L, 1L, 1L, "Test Recipe", LocalDateTime.now());
        Page<FavoriteResponseDto> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(userService.getCurrentUserId()).thenReturn(1L);
        when(favoriteService.getUserFavorites(anyLong(), any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<FavoriteResponseDto>>> result = favoriteController.getUserFavorites(0, 10, "id",
                "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Favorites retrieved successfully", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().getContent().size());
        assertEquals(1L, result.getBody().getData().getContent().getFirst().id());
        assertEquals("Test Recipe", result.getBody().getData().getContent().getFirst().recipeTitle());
        assertEquals(1, result.getBody().getData().getTotalElements());
    }

    @Test
    void isFavorite_True() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(favoriteService.isFavorite(anyLong(), anyLong()))
                .thenReturn(true);

        ResponseEntity<ApiResponse<Boolean>> result = favoriteController.isFavorite(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Favorite status retrieved successfully", result.getBody().getMessage());
        assertEquals(Boolean.TRUE, result.getBody().getData());
    }

    @Test
    void isFavorite_False() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(favoriteService.isFavorite(anyLong(), anyLong()))
                .thenReturn(false);

        ResponseEntity<ApiResponse<Boolean>> result = favoriteController.isFavorite(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Favorite status retrieved successfully", result.getBody().getMessage());
        assertNotEquals(Boolean.TRUE, result.getBody().getData());
    }
}
