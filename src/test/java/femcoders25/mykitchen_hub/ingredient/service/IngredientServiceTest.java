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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private Ingredient testIngredient;
    private IngredientDto testIngredientDto;
    private IngredientUpdateDto testIngredientUpdateDto;
    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setTitle("Test Recipe");

        testIngredient = new Ingredient();
        testIngredient.setId(1L);
        testIngredient.setName("Tomato");
        testIngredient.setAmount(2.0);
        testIngredient.setUnit("pieces");
        testIngredient.setRecipe(testRecipe);

        testIngredientDto = new IngredientDto("Tomato", 2.0, "pieces");
        testIngredientUpdateDto = new IngredientUpdateDto("Updated Tomato", 3.0, "kg");
    }

    @Test
    void getIngredientsByRecipeId_ExistingRecipeId_ReturnsIngredients() {
        List<Ingredient> ingredients = Arrays.asList(testIngredient);
        when(ingredientRepository.findByRecipeId(1L)).thenReturn(ingredients);

        List<IngredientResponseDto> result = ingredientService.getIngredientsByRecipeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tomato", result.get(0).name());
        assertEquals(2.0, result.get(0).amount());
        assertEquals("pieces", result.get(0).unit());
        assertEquals(1L, result.get(0).recipeId());

        verify(ingredientRepository).findByRecipeId(1L);
    }

    @Test
    void getIngredientsByRecipeId_NonExistingRecipeId_ReturnsEmptyList() {
        when(ingredientRepository.findByRecipeId(999L)).thenReturn(Collections.emptyList());

        List<IngredientResponseDto> result = ingredientService.getIngredientsByRecipeId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ingredientRepository).findByRecipeId(999L);
    }

    @Test
    void getIngredientById_ExistingId_ReturnsIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));

        IngredientResponseDto result = ingredientService.getIngredientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Tomato", result.name());
        assertEquals(2.0, result.amount());
        assertEquals("pieces", result.unit());
        assertEquals(1L, result.recipeId());

        verify(ingredientRepository).findById(1L);
    }

    @Test
    void getIngredientById_NonExistingId_ThrowsResourceNotFoundException() {
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.getIngredientById(999L));

        assertEquals("Ingredient not found with id: 999", exception.getMessage());
        verify(ingredientRepository).findById(999L);
    }

    @Test
    void createIngredient_ValidData_CreatesIngredient() {
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(testIngredient);

        IngredientResponseDto result = ingredientService.createIngredient(testIngredientDto, testRecipe);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Tomato", result.name());
        assertEquals(2.0, result.amount());
        assertEquals("pieces", result.unit());
        assertEquals(1L, result.recipeId());

        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void updateIngredient_ExistingId_UpdatesIngredient() {
        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setId(1L);
        updatedIngredient.setName("Updated Tomato");
        updatedIngredient.setAmount(3.0);
        updatedIngredient.setUnit("kg");
        updatedIngredient.setRecipe(testRecipe);

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(updatedIngredient);

        IngredientResponseDto result = ingredientService.updateIngredient(1L, testIngredientUpdateDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated Tomato", result.name());
        assertEquals(3.0, result.amount());
        assertEquals("kg", result.unit());
        assertEquals(1L, result.recipeId());

        verify(ingredientRepository).findById(1L);
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void updateIngredient_NonExistingId_ThrowsResourceNotFoundException() {
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.updateIngredient(999L, testIngredientUpdateDto));

        assertEquals("Ingredient not found with id: 999", exception.getMessage());
        verify(ingredientRepository).findById(999L);
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    void deleteIngredient_ExistingId_DeletesIngredient() {
        when(ingredientRepository.existsById(1L)).thenReturn(true);

        ingredientService.deleteIngredient(1L);

        verify(ingredientRepository).existsById(1L);
        verify(ingredientRepository).deleteById(1L);
    }

    @Test
    void deleteIngredient_NonExistingId_ThrowsResourceNotFoundException() {
        when(ingredientRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.deleteIngredient(999L));

        assertEquals("Ingredient not found with id: 999", exception.getMessage());
        verify(ingredientRepository).existsById(999L);
        verify(ingredientRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteIngredientsByRecipeId_ValidRecipeId_DeletesIngredients() {
        ingredientService.deleteIngredientsByRecipeId(1L);

        verify(ingredientRepository).deleteByRecipeId(1L);
    }

    @Test
    void createIngredientsFromDto_ValidList_CreatesIngredients() {
        List<IngredientDto> ingredientDtos = Arrays.asList(
                new IngredientDto("Tomato", 2.0, "pieces"),
                new IngredientDto("Onion", 1.0, "piece"));

        List<Ingredient> savedIngredients = Arrays.asList(
                createTestIngredient(1L, "Tomato", 2.0, "pieces"),
                createTestIngredient(2L, "Onion", 1.0, "piece"));

        when(ingredientRepository.saveAll(anyList())).thenReturn(savedIngredients);

        List<Ingredient> result = ingredientService.createIngredientsFromDto(ingredientDtos, testRecipe);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tomato", result.get(0).getName());
        assertEquals("Onion", result.get(1).getName());

        verify(ingredientRepository).saveAll(anyList());
    }

    @Test
    void createIngredientsFromDto_EmptyList_ReturnsEmptyList() {
        List<IngredientDto> emptyList = Collections.emptyList();

        List<Ingredient> result = ingredientService.createIngredientsFromDto(emptyList, testRecipe);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ingredientRepository, never()).saveAll(anyList());
    }

    @Test
    void createIngredientsFromDto_NullList_ReturnsEmptyList() {
        List<Ingredient> result = ingredientService.createIngredientsFromDto(null, testRecipe);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ingredientRepository, never()).saveAll(anyList());
    }

    @Test
    void updateIngredient_PartialUpdate_UpdatesOnlyProvidedFields() {
        IngredientUpdateDto partialUpdate = new IngredientUpdateDto("Updated Name", null, null);

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> {
            Ingredient saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        IngredientResponseDto result = ingredientService.updateIngredient(1L, partialUpdate);

        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals(2.0, result.amount());
        assertEquals("pieces", result.unit());

        verify(ingredientRepository).findById(1L);
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    private Ingredient createTestIngredient(Long id, String name, Double amount, String unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setAmount(amount);
        ingredient.setUnit(unit);
        ingredient.setRecipe(testRecipe);
        return ingredient;
    }
}