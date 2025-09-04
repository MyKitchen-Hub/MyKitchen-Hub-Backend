package femcoders25.mykitchen_hub.favorite.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteRequestDto;
import femcoders25.mykitchen_hub.favorite.dto.FavoriteResponseDto;
import femcoders25.mykitchen_hub.favorite.service.FavoriteService;
import femcoders25.mykitchen_hub.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Favorite recipes management APIs")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add recipe to favorites", description = "Adds a recipe to user's favorites. Provide the recipe ID to add.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recipe added to favorites successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - invalid data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Favorite request data", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteRequestDto.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Add recipe to favorites", value = "{\"recipeId\":1}")))
    public ResponseEntity<ApiResponse<FavoriteResponseDto>> addToFavorites(
            @Valid @RequestBody FavoriteRequestDto request) {
        Long userId = userService.getCurrentUserId();
        FavoriteResponseDto response = favoriteService.addToFavorites(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe added to favorites successfully", response));
    }

    @DeleteMapping("/{recipeId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Remove recipe from favorites", description = "Removes a recipe from user's favorites")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recipe removed from favorites successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Favorite not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<String>> removeFromFavorites(
            @Parameter(description = "Recipe ID") @PathVariable Long recipeId) {

        Long userId = userService.getCurrentUserId();
        favoriteService.removeFromFavorites(userId, recipeId);
        return ResponseEntity.ok(ApiResponse.success("Recipe removed from favorites successfully", null));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user favorites", description = "Retrieves a paginated list of user's favorite recipes")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Favorites retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Page<FavoriteResponseDto>>> getUserFavorites(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Long userId = userService.getCurrentUserId();
        Page<FavoriteResponseDto> favorites = favoriteService.getUserFavorites(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Favorites retrieved successfully", favorites));
    }

    @GetMapping("/{recipeId}/check")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Check if recipe is favorite", description = "Checks if a recipe is in user's favorites")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Favorite status retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @Parameter(description = "Recipe ID") @PathVariable Long recipeId) {

        Long userId = userService.getCurrentUserId();
        boolean isFavorite = favoriteService.isFavorite(userId, recipeId);
        return ResponseEntity.ok(ApiResponse.success("Favorite status retrieved successfully", isFavorite));
    }

}
