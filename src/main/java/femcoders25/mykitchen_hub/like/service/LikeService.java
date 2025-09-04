package femcoders25.mykitchen_hub.like.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;
import femcoders25.mykitchen_hub.like.entity.Like;
import femcoders25.mykitchen_hub.like.repository.LikeRepository;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public LikeStatsDto likeRecipe(Long userId, Long recipeId) {
        return toggleLike(userId, recipeId, true);
    }

    public LikeStatsDto dislikeRecipe(Long userId, Long recipeId) {
        return toggleLike(userId, recipeId, false);
    }

    private LikeStatsDto toggleLike(Long userId, Long recipeId, boolean isLike) {
        User user = getUserById(userId);
        Recipe recipe = getRecipeById(recipeId);

        Optional<Like> existingLike = likeRepository.findByUserAndRecipe(user, recipe);

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            if (like.getIsLike() == isLike) {
                likeRepository.delete(like);
            } else {
                like.setIsLike(isLike);
                likeRepository.save(like);
            }
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setRecipe(recipe);
            newLike.setIsLike(isLike);
            likeRepository.save(newLike);
        }

        return getLikeStats(userId, recipeId);
    }

    public LikeStatsDto getLikeStats(Long userId, Long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        User user = getUserById(userId);

        long likesCount = likeRepository.countLikesByRecipe(recipe);
        long dislikesCount = likeRepository.countDislikesByRecipe(recipe);

        Optional<Like> userLike = likeRepository.findByUserAndRecipe(user, recipe);
        boolean userLiked = userLike.isPresent() && userLike.get().getIsLike();
        boolean userDisliked = userLike.isPresent() && !userLike.get().getIsLike();

        return new LikeStatsDto(likesCount, dislikesCount, userLiked, userDisliked);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));
    }

    public long getLikesCount(Long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        return likeRepository.countLikesByRecipe(recipe);
    }

    public long getDislikesCount(Long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        return likeRepository.countDislikesByRecipe(recipe);
    }
}
