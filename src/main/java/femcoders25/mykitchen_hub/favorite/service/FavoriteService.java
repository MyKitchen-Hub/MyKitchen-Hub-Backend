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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

        private final UserRepository userRepository;
        private final RecipeRepository recipeRepository;
        private final FavoriteRepository favoriteRepository;
        private final FavoriteMapper favoriteMapper;

        public FavoriteResponseDto addToFavorites(Long userId, FavoriteRequestDto request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                Recipe recipe = recipeRepository.findById(request.recipeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + request.recipeId()));

                if (favoriteRepository.existsByUserAndRecipe(user, recipe)) {
                        throw new IllegalArgumentException("Recipe is already in favorites");
                }

                Favorite favorite = new Favorite();
                favorite.setUser(user);
                favorite.setRecipe(recipe);

                Favorite savedFavorite = favoriteRepository.save(favorite);
                return favoriteMapper.toResponse(savedFavorite);
        }

        public void removeFromFavorites(Long userId, Long recipeId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                Recipe recipe = recipeRepository.findById(recipeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));

                if (!favoriteRepository.existsByUserAndRecipe(user, recipe)) {
                        throw new ResourceNotFoundException("Favorite not found for user and recipe");
                }

                favoriteRepository.deleteByUserAndRecipe(user, recipe);
        }

        @Transactional(readOnly = true)
        public Page<FavoriteResponseDto> getUserFavorites(Long userId, Pageable pageable) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                Page<Favorite> favorites = favoriteRepository.findByUser(user, pageable);
                return favorites.map(favoriteMapper::toResponse);
        }

        @Transactional(readOnly = true)
        public boolean isFavorite(Long userId, Long recipeId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                Recipe recipe = recipeRepository.findById(recipeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));

                return favoriteRepository.existsByUserAndRecipe(user, recipe);
        }
}
