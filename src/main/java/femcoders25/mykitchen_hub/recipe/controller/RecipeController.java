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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipe Management", description = "Recipe management endpoints for creating, updating, and retrieving recipes")
public class RecipeController {

        private final RecipeService recipeService;

        @Operation(summary = "Create a new recipe", description = "Creates a new recipe with optional image upload using form fields. All fields are displayed as input fields in Swagger UI.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recipe created successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or file", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(consumes = "multipart/form-data")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RecipeResponseDto>> createRecipe(
                        @Parameter(description = "Recipe title (3-100 characters)", required = true, example = "Chocolate Cake") @RequestParam("title") @NotBlank(message = "Title is required") @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters") String title,
                        @Parameter(description = "Recipe description (10-2000 characters)", required = true, example = "A delicious homemade chocolate cake recipe") @RequestParam("description") @NotBlank(message = "Description is required") @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters") String description,
                        @Parameter(description = "Ingredients as JSON string. Must be a valid JSON array of ingredient objects with 'name', 'amount', and 'unit' properties. Example: [{\"name\":\"Flour\",\"amount\":200,\"unit\":\"g\"},{\"name\":\"Sugar\",\"amount\":100,\"unit\":\"g\"}]", required = true, example = "[{\"name\":\"Flour\",\"amount\":200,\"unit\":\"g\"},{\"name\":\"Sugar\",\"amount\":100,\"unit\":\"g\"}]", schema = @Schema(type = "string", format = "json")) @RequestParam("ingredients") @NotBlank(message = "Ingredients are required") String ingredients,
                        @Parameter(description = "Recipe image file (optional)", schema = @Schema(type = "string", format = "binary")) @RequestParam(value = "image", required = false) MultipartFile image,
                        @Parameter(description = "Recipe tag (optional)", example = "dessert") @RequestParam(value = "tag", required = false) String tag)
                        throws IOException {

                log.info("Creating new recipe: {}", title);
                RecipeResponseDto response = recipeService.createRecipe(title, description, ingredients, image, tag);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Recipe created successfully", response));
        }

        @Operation(summary = "Create a new recipe (JSON)", description = "Creates a new recipe using JSON data (without image upload)")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recipe created successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(value = "/json", consumes = "application/json")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RecipeResponseDto>> createRecipeJson(
                        @Parameter(description = "Recipe data") @RequestBody RecipeCreateDto recipeDto) {
                log.info("Creating new recipe from JSON: {}", recipeDto.title());
                RecipeResponseDto response = recipeService.createRecipe(recipeDto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Recipe created successfully", response));
        }

        @Operation(summary = "Get all recipes", description = "Retrieves a paginated list of all recipes with sorting options")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipes retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @GetMapping
        public ResponseEntity<ApiResponse<Page<RecipeListDto>>> getAllRecipes(
                        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
                        @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<RecipeListDto> recipes = recipeService.getAllRecipes(pageable);
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

        @Operation(summary = "Update recipe", description = "Updates an existing recipe with optional image upload using form fields. All fields are displayed as input fields in Swagger UI.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or file", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping(value = "/{id}", consumes = "multipart/form-data")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipe(
                        @Parameter(description = "Recipe ID") @PathVariable Long id,
                        @Parameter(description = "Recipe title (optional, 3-100 characters)", example = "Updated Chocolate Cake") @RequestParam(value = "title", required = false) @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters") String title,
                        @Parameter(description = "Recipe description (optional, 10-2000 characters)", example = "An updated delicious homemade chocolate cake recipe") @RequestParam(value = "description", required = false) @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters") String description,
                        @Parameter(description = "Recipe ingredients as JSON string (optional). Must be a valid JSON array of ingredient objects with 'name', 'amount', and 'unit' properties. Example: [{\"name\":\"Flour\",\"amount\":300,\"unit\":\"g\"}]", example = "[{\"name\":\"Flour\",\"amount\":300,\"unit\":\"g\"}]", schema = @Schema(type = "string", format = "json")) @RequestParam(value = "ingredients", required = false) String ingredients,
                        @Parameter(description = "Recipe image file (optional)", schema = @Schema(type = "string", format = "binary")) @RequestParam(value = "image", required = false) MultipartFile image,
                        @Parameter(description = "Recipe tag (optional)", example = "dessert") @RequestParam(value = "tag", required = false) String tag)
                        throws IOException {

                log.info("Updating recipe with id: {}", id);
                RecipeResponseDto response = recipeService.updateRecipe(id, title, description, ingredients, image,
                                tag);
                return ResponseEntity.ok(ApiResponse.success("Recipe updated successfully", response));
        }

        @Operation(summary = "Update recipe (JSON)", description = "Updates an existing recipe using JSON data (without image upload)")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recipe updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping(value = "/{id}/json", consumes = "application/json")
        @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipeJson(
                        @Parameter(description = "Recipe ID") @PathVariable Long id,
                        @Parameter(description = "Recipe data") @RequestBody RecipeUpdateDto recipeDto) {
                log.info("Updating recipe with id: {} from JSON", id);
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
                                .ok(ApiResponse.success("Recipe deleted successfully",
                                                "Recipe with id " + id + " has been deleted"));
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
                                : "Found " + recipes.getTotalElements() + " recipes with ingredient containing: "
                                                + ingredient;

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
