package femcoders25.mykitchen_hub.recipe.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.recipe.dto.RecipeCreateDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeListDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeResponseDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeUpdateDto;
import femcoders25.mykitchen_hub.recipe.service.RecipeService;
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
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> createRecipe(@Valid @RequestBody RecipeCreateDto recipeDto) {
        log.info("Creating new recipe: {}", recipeDto.title());
        RecipeResponseDto response = recipeService.createRecipe(recipeDto);
        return ResponseEntity.ok(ApiResponse.success("Recipe created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponseDto> recipes = recipeService.getAllRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes retrieved successfully", recipes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> getRecipeById(@PathVariable Long id) {
        RecipeResponseDto recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe retrieved successfully", recipe));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeUpdateDto recipeDto) {
        log.info("Updating recipe with id: {}", id);
        RecipeResponseDto response = recipeService.updateRecipe(id, recipeDto);
        return ResponseEntity.ok(ApiResponse.success("Recipe updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRecipe(@PathVariable Long id) {
        log.info("Deleting recipe with id: {}", id);
        recipeService.deleteRecipe(id);
        return ResponseEntity
                .ok(ApiResponse.success("Recipe deleted successfully", "Recipe with id " + id + " has been deleted"));
    }

    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> searchRecipesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching recipes by title: {}", title);
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDto> recipes = recipeService.searchRecipesByTitle(title, pageable);

        String message = recipes.isEmpty()
                ? "No recipes found with title containing: " + title
                : "Found " + recipes.getTotalElements() + " recipes with title containing: " + title;

        return ResponseEntity.ok(ApiResponse.success(message, recipes));
    }

    @GetMapping("/search/ingredient")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> searchRecipesByIngredient(
            @RequestParam String ingredient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching recipes by ingredient: {}", ingredient);
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDto> recipes = recipeService.searchRecipesByIngredient(ingredient, pageable);

        String message = recipes.isEmpty()
                ? "No recipes found with ingredient containing: " + ingredient
                : "Found " + recipes.getTotalElements() + " recipes with ingredient containing: " + ingredient;

        return ResponseEntity.ok(ApiResponse.success(message, recipes));
    }

    @GetMapping("/search/tag")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> searchRecipesByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching recipes by tag: {}", tag);
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDto> recipes = recipeService.searchRecipesByTag(tag, pageable);

        String message = recipes.isEmpty()
                ? "No recipes found with tag containing: " + tag
                : "Found " + recipes.getTotalElements() + " recipes with tag containing: " + tag;

        return ResponseEntity.ok(ApiResponse.success(message, recipes));
    }
}
