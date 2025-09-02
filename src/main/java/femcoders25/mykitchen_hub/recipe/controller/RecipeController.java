package femcoders25.mykitchen_hub.recipe.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.recipe.dto.RecipeCreateDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeListDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeResponseDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeUpdateDto;
import femcoders25.mykitchen_hub.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipe Management", description = "Recipe management endpoints for creating, updating, and retrieving recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "Create a new recipe", description = "Creates a new recipe (Authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe created successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> createRecipe(@Valid @RequestBody RecipeCreateDto recipeDto) {
        log.info("Creating new recipe: {}", recipeDto.title());
        RecipeResponseDto response = recipeService.createRecipe(recipeDto);
        return ResponseEntity.ok(ApiResponse.success("Recipe created successfully", response));
    }

    @Operation(summary = "Get all recipes", description = "Retrieves a paginated list of all recipes with sorting options")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipes retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> getAllRecipes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponseDto> recipes = recipeService.getAllRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes retrieved successfully", recipes));
    }

    @Operation(summary = "Get recipe by ID", description = "Retrieves a specific recipe by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> getRecipeById(
            @Parameter(description = "Recipe ID") @PathVariable Long id) {
        RecipeResponseDto recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe retrieved successfully", recipe));
    }

    @Operation(summary = "Update recipe", description = "Updates an existing recipe (Authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipe(
            @Parameter(description = "Recipe ID") @PathVariable Long id,
            @Valid @RequestBody RecipeUpdateDto recipeDto) {
        log.info("Updating recipe with id: {}", id);
        RecipeResponseDto response = recipeService.updateRecipe(id, recipeDto);
        return ResponseEntity.ok(ApiResponse.success("Recipe updated successfully", response));
    }

    @Operation(summary = "Delete recipe", description = "Deletes a recipe (Authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRecipe(
            @Parameter(description = "Recipe ID") @PathVariable Long id) {
        log.info("Deleting recipe with id: {}", id);
        recipeService.deleteRecipe(id);
        return ResponseEntity
                .ok(ApiResponse.success("Recipe deleted successfully", "Recipe with id " + id + " has been deleted"));
    }

    @Operation(summary = "Search recipes by title", description = "Searches for recipes containing the specified title")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> searchRecipesByTitle(
            @Parameter(description = "Title to search for") @RequestParam String title,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching recipes by title: {}", title);
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDto> recipes = recipeService.searchRecipesByTitle(title, pageable);

        String message = recipes.isEmpty()
                ? "No recipes found with title containing: " + title
                : "Found " + recipes.getTotalElements() + " recipes with title containing: " + title;

        return ResponseEntity.ok(ApiResponse.success(message, recipes));
    }

    @Operation(summary = "Search recipes by ingredient", description = "Searches for recipes containing the specified ingredient")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/search/ingredient")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> searchRecipesByIngredient(
            @Parameter(description = "Ingredient to search for") @RequestParam String ingredient,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching recipes by ingredient: {}", ingredient);
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDto> recipes = recipeService.searchRecipesByIngredient(ingredient, pageable);

        String message = recipes.isEmpty()
                ? "No recipes found with ingredient containing: " + ingredient
                : "Found " + recipes.getTotalElements() + " recipes with ingredient containing: " + ingredient;

        return ResponseEntity.ok(ApiResponse.success(message, recipes));
    }

    @Operation(summary = "Search recipes by tag", description = "Searches for recipes containing the specified tag")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/search/tag")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> searchRecipesByTag(
            @Parameter(description = "Tag to search for") @RequestParam String tag,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching recipes by tag: {}", tag);
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDto> recipes = recipeService.searchRecipesByTag(tag, pageable);

        String message = recipes.isEmpty()
                ? "No recipes found with tag containing: " + tag
                : "Found " + recipes.getTotalElements() + " recipes with tag containing: " + tag;

        return ResponseEntity.ok(ApiResponse.success(message, recipes));
    }
}
