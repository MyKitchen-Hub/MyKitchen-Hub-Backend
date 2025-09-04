package femcoders25.mykitchen_hub.ingredient.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientUpdateDto;
import femcoders25.mykitchen_hub.ingredient.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientControllerTest {

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private IngredientController ingredientController;

    private IngredientResponseDto ingredientResponse;
    private List<IngredientResponseDto> ingredientsList;

    @BeforeEach
    void setUp() {
        ingredientResponse = new IngredientResponseDto(1L, "Test Ingredient", 100.0, "g", 1L);
        new IngredientUpdateDto("Updated Ingredient", 200.0, "kg");
        ingredientsList = Collections.singletonList(ingredientResponse);
    }

    @Test
    void testDeleteIngredient() {
        doNothing().when(ingredientService).deleteIngredient(1L);

        ResponseEntity<ApiResponse<String>> response = ingredientController.deleteIngredient(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ingredient deleted successfully", response.getBody().getMessage());
        verify(ingredientService).deleteIngredient(1L);
    }

    @Test
    void testGetIngredientById() {
        when(ingredientService.getIngredientById(1L)).thenReturn(ingredientResponse);

        ResponseEntity<ApiResponse<IngredientResponseDto>> response = ingredientController.getIngredientById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ingredient retrieved successfully", response.getBody().getMessage());
        assertEquals(ingredientResponse, response.getBody().getData());
        verify(ingredientService).getIngredientById(1L);
    }

    @Test
    void testGetIngredientsByRecipeId() {
        when(ingredientService.getIngredientsByRecipeId(1L)).thenReturn(ingredientsList);

        ResponseEntity<ApiResponse<List<IngredientResponseDto>>> response = ingredientController
                .getIngredientsByRecipeId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ingredients retrieved successfully", response.getBody().getMessage());
        assertEquals(ingredientsList, response.getBody().getData());
        verify(ingredientService).getIngredientsByRecipeId(1L);
    }

    @Test
    void testUpdateIngredient() {
        when(ingredientService.updateIngredient(eq(1L), any(IngredientUpdateDto.class))).thenReturn(ingredientResponse);

        ResponseEntity<ApiResponse<IngredientResponseDto>> response = ingredientController.updateIngredient(1L,
                new IngredientUpdateDto("Updated Flour", 300.0, "g"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ingredient updated successfully", response.getBody().getMessage());
        assertEquals(ingredientResponse, response.getBody().getData());
        verify(ingredientService).updateIngredient(eq(1L), any(IngredientUpdateDto.class));
    }
}
