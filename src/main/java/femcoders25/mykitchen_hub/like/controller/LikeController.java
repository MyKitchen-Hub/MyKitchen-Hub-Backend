package femcoders25.mykitchen_hub.like.controller;

import femcoders25.mykitchen_hub.like.dto.LikeStatsDto;
import femcoders25.mykitchen_hub.like.service.LikeService;
import femcoders25.mykitchen_hub.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Like Management", description = "Like and dislike management endpoints for recipes")
public class LikeController {

        private final LikeService likeService;
        private final UserService userService;

        @Operation(summary = "Like a recipe", description = "Adds a like to the specified recipe for the current user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe liked successfully", content = @Content(schema = @Schema(implementation = LikeStatsDto.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid recipe ID"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required")
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping("/{recipeId}/like")
        public ResponseEntity<LikeStatsDto> likeRecipe(
                        @Parameter(description = "ID of the recipe to like", required = true) @PathVariable @Positive Long recipeId) {
                Long userId = userService.getCurrentUserId();
                LikeStatsDto stats = likeService.likeRecipe(userId, recipeId);
                return ResponseEntity.ok(stats);
        }

        @Operation(summary = "Dislike a recipe", description = "Adds a dislike to the specified recipe for the current user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe disliked successfully", content = @Content(schema = @Schema(implementation = LikeStatsDto.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid recipe ID"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required")
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping("/{recipeId}/dislike")
        public ResponseEntity<LikeStatsDto> dislikeRecipe(
                        @Parameter(description = "ID of the recipe to dislike", required = true) @PathVariable @Positive Long recipeId) {
                Long userId = userService.getCurrentUserId();
                LikeStatsDto stats = likeService.dislikeRecipe(userId, recipeId);
                return ResponseEntity.ok(stats);
        }

        @Operation(summary = "Get recipe like statistics", description = "Retrieves like and dislike statistics for the specified recipe. For authenticated users, also includes their like/dislike status.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe statistics retrieved successfully", content = @Content(schema = @Schema(implementation = LikeStatsDto.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid recipe ID"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found")
        })
        @GetMapping("/{recipeId}/stats")
        public ResponseEntity<LikeStatsDto> getRecipeStats(
                        @Parameter(description = "ID of the recipe to get statistics for", required = true) @PathVariable @Positive Long recipeId) {
                Long userId = null;
                try {
                        userId = userService.getCurrentUserId();
                } catch (Exception e) {
                }
                LikeStatsDto stats = likeService.getLikeStats(userId, recipeId);
                return ResponseEntity.ok(stats);
        }
}
