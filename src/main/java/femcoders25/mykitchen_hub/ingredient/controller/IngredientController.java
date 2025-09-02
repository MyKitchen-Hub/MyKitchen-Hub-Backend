package femcoders25.mykitchen_hub.ingredient.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientUpdateDto;
import femcoders25.mykitchen_hub.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredient Management", description = "Ingredient management endpoints for creating, updating, and retrieving ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(summary = "Get ingredients by recipe ID", description = "Retrieves all ingredients for a specific recipe")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<ApiResponse<List<IngredientResponseDto>>> getIngredientsByRecipeId(
            @Parameter(description = "Recipe ID") @PathVariable Long recipeId) {
        List<IngredientResponseDto> ingredients = ingredientService.getIngredientsByRecipeId(recipeId);
        return ResponseEntity.ok(ApiResponse.success("Ingredients retrieved successfully", ingredients));
    }

    @Operation(summary = "Get ingredient by ID", description = "Retrieves a specific ingredient by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingredient retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponseDto>> getIngredientById(
            @Parameter(description = "Ingredient ID") @PathVariable Long id) {
        IngredientResponseDto ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient retrieved successfully", ingredient));
    }

    @Operation(summary = "Update ingredient", description = "Updates an existing ingredient (Authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingredient updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponseDto>> updateIngredient(
            @Parameter(description = "Ingredient ID") @PathVariable Long id,
            @Valid @RequestBody IngredientUpdateDto updateDto) {
        IngredientResponseDto updatedIngredient = ingredientService.updateIngredient(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("Ingredient updated successfully", updatedIngredient));
    }

    @Operation(summary = "Delete ingredient", description = "Deletes an ingredient (Authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingredient deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteIngredient(
            @Parameter(description = "Ingredient ID") @PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient deleted successfully"));
    }
}
