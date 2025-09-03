package femcoders25.mykitchen_hub.recipe.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeCreateDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeResponseDto;
import femcoders25.mykitchen_hub.recipe.dto.RecipeUpdateDto;
import femcoders25.mykitchen_hub.recipe.service.RecipeService;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private RecipeCreateDto createDto;
    private RecipeUpdateDto updateDto;
    private RecipeResponseDto responseDto;
    private Page<RecipeResponseDto> recipePage;

    @BeforeEach
    void setUp() {
        createDto = new RecipeCreateDto("Test Recipe", "Test Description", List.<IngredientDto>of(), null, "Test Tag");
        updateDto = new RecipeUpdateDto("Updated Recipe", "Updated Description", List.<IngredientDto>of(), null,
                "Updated Tag");
        responseDto = new RecipeResponseDto(1L, "Test Recipe", "Test Description", List.<IngredientDto>of(), null,
                "Test Tag",
                null, null, 1L, "testuser");
        recipePage = new PageImpl<>(List.<RecipeResponseDto>of(responseDto));
    }

    @Test
    void testCreateRecipe() throws IOException {
        when(recipeService.createRecipe("Test Recipe", "Test Description", "[]", null, "Test Tag"))
                .thenReturn(responseDto);

        ResponseEntity<ApiResponse<RecipeResponseDto>> response = recipeController.createRecipe(
                "Test Recipe", "Test Description", "[]", null, "Test Tag");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipe created successfully", response.getBody().getMessage());
        assertEquals(responseDto, response.getBody().getData());
        verify(recipeService).createRecipe("Test Recipe", "Test Description", "[]", null, "Test Tag");
    }

    @Test
    void testCreateRecipeJson() {
        when(recipeService.createRecipe(createDto)).thenReturn(responseDto);

        ResponseEntity<ApiResponse<RecipeResponseDto>> response = recipeController.createRecipeJson(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipe created successfully", response.getBody().getMessage());
        assertEquals(responseDto, response.getBody().getData());
        verify(recipeService).createRecipe(createDto);
    }

    @Test
    void testDeleteRecipe() {
        doNothing().when(recipeService).deleteRecipe(1L);

        ResponseEntity<ApiResponse<String>> response = recipeController.deleteRecipe(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipe deleted successfully", response.getBody().getMessage());
        verify(recipeService).deleteRecipe(1L);
    }

    @Test
    void testGetAllRecipes() {
        when(recipeService.getAllRecipes(any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> response = recipeController.getAllRecipes(0, 10, "id",
                "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipes retrieved successfully", response.getBody().getMessage());
        assertEquals(recipePage, response.getBody().getData());
        verify(recipeService).getAllRecipes(any(Pageable.class));
    }

    @Test
    void testGetRecipeById() {
        when(recipeService.getRecipeById(1L)).thenReturn(responseDto);

        ResponseEntity<ApiResponse<RecipeResponseDto>> response = recipeController.getRecipeById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipe retrieved successfully", response.getBody().getMessage());
        assertEquals(responseDto, response.getBody().getData());
        verify(recipeService).getRecipeById(1L);
    }

    @Test
    void testSearchRecipesByIngredient() {
        when(recipeService.searchRecipesByIngredient(eq("tomato"), any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> response = recipeController
                .searchRecipesByIngredient("tomato", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recipeService).searchRecipesByIngredient(eq("tomato"), any(Pageable.class));
    }

    @Test
    void testSearchRecipesByTag() {
        when(recipeService.searchRecipesByTag(eq("italian"), any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> response = recipeController.searchRecipesByTag("italian",
                0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recipeService).searchRecipesByTag(eq("italian"), any(Pageable.class));
    }

    @Test
    void testSearchRecipesByTitle() {
        when(recipeService.searchRecipesByTitle(eq("pasta"), any(Pageable.class))).thenReturn(recipePage);

        ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> response = recipeController.searchRecipesByTitle("pasta",
                0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recipeService).searchRecipesByTitle(eq("pasta"), any(Pageable.class));
    }

    @Test
    void testUpdateRecipe() throws IOException {
        when(recipeService.updateRecipe(eq(1L), eq("Updated Recipe"), eq("Updated Description"), eq(null), eq(null),
                eq("Updated Tag"))).thenReturn(responseDto);

        ResponseEntity<ApiResponse<RecipeResponseDto>> response = recipeController.updateRecipe(
                1L, "Updated Recipe", "Updated Description", null, null, "Updated Tag");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipe updated successfully", response.getBody().getMessage());
        assertEquals(responseDto, response.getBody().getData());
        verify(recipeService).updateRecipe(eq(1L), eq("Updated Recipe"), eq("Updated Description"), eq(null), eq(null),
                eq("Updated Tag"));
    }

    @Test
    void testUpdateRecipeJson() {
        when(recipeService.updateRecipe(1L, updateDto)).thenReturn(responseDto);

        ResponseEntity<ApiResponse<RecipeResponseDto>> response = recipeController.updateRecipeJson(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recipe updated successfully", response.getBody().getMessage());
        assertEquals(responseDto, response.getBody().getData());
        verify(recipeService).updateRecipe(1L, updateDto);
    }
}
