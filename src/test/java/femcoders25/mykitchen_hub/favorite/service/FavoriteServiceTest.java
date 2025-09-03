package femcoders25.mykitchen_hub.favorite.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteMapper;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteRequestDto;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteResponseDto;
import femcoders25.mykitchen_hub.favorite.entity.Favorite;
import femcoders25.mykitchen_hub.favorite.repository.FavoriteRepository;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteService favoriteService;

    private User user;
    private Recipe recipe;
    private Favorite favorite;
    private FavoriteRequestDto favoriteRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");

        favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setRecipe(recipe);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteRequest = new FavoriteRequestDto(1L);
    }

    @Test
    void addToFavorites_Success() {
        FavoriteResponseDto expectedResponse = new FavoriteResponseDto(1L, 1L, 1L, "Test Recipe", LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(favoriteRepository.existsByUserAndRecipe(user, recipe)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toResponse(favorite)).thenReturn(expectedResponse);

        FavoriteResponseDto response = favoriteService.addToFavorites(1L, favoriteRequest);

        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(favoriteRepository).save(any(Favorite.class));
        verify(favoriteMapper).toResponse(favorite);
    }

    @Test
    void addToFavorites_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> favoriteService.addToFavorites(1L, favoriteRequest));
    }

    @Test
    void addToFavorites_RecipeNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> favoriteService.addToFavorites(1L, favoriteRequest));
    }

    @Test
    void addToFavorites_AlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(favoriteRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> favoriteService.addToFavorites(1L, favoriteRequest));
    }

    @Test
    void removeFromFavorites_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(favoriteRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        favoriteService.removeFromFavorites(1L, 1L);

        verify(favoriteRepository).existsByUserAndRecipe(user, recipe);
        verify(favoriteRepository).deleteByUserAndRecipe(user, recipe);
    }

    @Test
    void removeFromFavorites_FavoriteNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(favoriteRepository.existsByUserAndRecipe(user, recipe)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFromFavorites(1L, 1L));

        verify(favoriteRepository).existsByUserAndRecipe(user, recipe);
        verify(favoriteRepository, never()).deleteByUserAndRecipe(user, recipe);
    }

    @Test
    void getUserFavorites_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Favorite> favoritePage = new PageImpl<>(List.of(favorite));
        FavoriteResponseDto expectedResponse = new FavoriteResponseDto(1L, 1L, 1L, "Test Recipe", LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUser(user, pageable)).thenReturn(favoritePage);
        when(favoriteMapper.toResponse(favorite)).thenReturn(expectedResponse);

        Page<FavoriteResponseDto> response = favoriteService.getUserFavorites(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(expectedResponse, response.getContent().get(0));

        verify(favoriteMapper).toResponse(favorite);
    }

    @Test
    void isFavorite_True() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(favoriteRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        boolean result = favoriteService.isFavorite(1L, 1L);

        assertTrue(result);
    }

    @Test
    void isFavorite_False() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(favoriteRepository.existsByUserAndRecipe(user, recipe)).thenReturn(false);

        boolean result = favoriteService.isFavorite(1L, 1L);

        assertFalse(result);
    }
}
