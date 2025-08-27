package femcoders25.mykitchen_hub.ingredient.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientUpdateDto;
import femcoders25.mykitchen_hub.ingredient.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<ApiResponse<List<IngredientResponseDto>>> getIngredientsByRecipeId(
            @PathVariable Long recipeId) {
        List<IngredientResponseDto> ingredients = ingredientService.getIngredientsByRecipeId(recipeId);
        return ResponseEntity.ok(ApiResponse.success("Ingredients retrieved successfully", ingredients));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponseDto>> getIngredientById(@PathVariable Long id) {
        IngredientResponseDto ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient retrieved successfully", ingredient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponseDto>> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientUpdateDto updateDto) {
        IngredientResponseDto updatedIngredient = ingredientService.updateIngredient(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("Ingredient updated successfully", updatedIngredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient deleted successfully"));
    }
}
