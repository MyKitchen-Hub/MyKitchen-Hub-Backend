package femcoders25.mykitchen_hub.ingredient.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientUpdateDto;
import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.ingredient.repository.IngredientRepository;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private Ingredient ingredient;
    private IngredientDto ingredientDto;
    private IngredientUpdateDto updateDto;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");

        ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Test Ingredient");
        ingredient.setAmount(100.0);
        ingredient.setUnit("g");
        ingredient.setRecipe(recipe);

        ingredientDto = new IngredientDto("Test Ingredient", 100.0, "g");
        updateDto = new IngredientUpdateDto("Updated Ingredient", 200.0, "kg");
        IngredientResponseDto responseDto = new IngredientResponseDto(1L, "Test Ingredient", 100.0, "g", 1L);
    }

    @Test
    void testCreateIngredient() {
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);

        IngredientResponseDto result = ingredientService.createIngredient(ingredientDto, recipe);

        assertNotNull(result);
        assertEquals(ingredient.getName(), result.name());
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void testDeleteIngredient() {
        when(ingredientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ingredientRepository).deleteById(1L);

        assertDoesNotThrow(() -> ingredientService.deleteIngredient(1L));
        verify(ingredientRepository).deleteById(1L);
    }

    @Test
    void testDeleteIngredient_NotFound() {
        when(ingredientRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.deleteIngredient(1L));
        verify(ingredientRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteIngredientsByRecipeId() {
        doNothing().when(ingredientRepository).deleteByRecipeId(1L);

        assertDoesNotThrow(() -> ingredientService.deleteIngredientsByRecipeId(1L));
        verify(ingredientRepository).deleteByRecipeId(1L);
    }

    @Test
    void testGetIngredientById() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        IngredientResponseDto result = ingredientService.getIngredientById(1L);

        assertNotNull(result);
        assertEquals(ingredient.getName(), result.name());
        verify(ingredientRepository).findById(1L);
    }

    @Test
    void testGetIngredientById_NotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getIngredientById(1L));
        verify(ingredientRepository).findById(1L);
    }

    @Test
    void testGetIngredientsByRecipeId() {
        List<Ingredient> ingredients = Collections.singletonList(ingredient);
        when(ingredientRepository.findByRecipeId(1L)).thenReturn(ingredients);

        List<IngredientResponseDto> result = ingredientService.getIngredientsByRecipeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ingredientRepository).findByRecipeId(1L);
    }

    @Test
    void testUpdateIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);

        IngredientResponseDto result = ingredientService.updateIngredient(1L, updateDto);

        assertNotNull(result);
        verify(ingredientRepository).findById(1L);
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void testUpdateIngredient_NotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.updateIngredient(1L, updateDto));
        verify(ingredientRepository).findById(1L);
        verify(ingredientRepository, never()).save(any());
    }
}
