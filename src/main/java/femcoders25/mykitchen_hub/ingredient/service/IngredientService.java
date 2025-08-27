package femcoders25.mykitchen_hub.ingredient.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientResponseDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientUpdateDto;
import femcoders25.mykitchen_hub.ingredient.dto.IngredientMapper;
import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.ingredient.repository.IngredientRepository;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<IngredientResponseDto> getIngredientsByRecipeId(Long recipeId) {
        List<Ingredient> ingredients = ingredientRepository.findByRecipeId(recipeId);
        return ingredients.stream()
                .map(IngredientMapper::toIngredientResponseDto)
                .collect(Collectors.toList());
    }

    public IngredientResponseDto getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
        return IngredientMapper.toIngredientResponseDto(ingredient);
    }

    public IngredientResponseDto createIngredient(IngredientDto ingredientDto, Recipe recipe) {
        Ingredient ingredient = IngredientMapper.toIngredient(ingredientDto, recipe);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return IngredientMapper.toIngredientResponseDto(savedIngredient);
    }

    public IngredientResponseDto updateIngredient(Long id, IngredientUpdateDto updateDto) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
        
        IngredientMapper.updateIngredientFromDto(ingredient, updateDto);
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return IngredientMapper.toIngredientResponseDto(updatedIngredient);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }

    public void deleteIngredientsByRecipeId(Long recipeId) {
        ingredientRepository.deleteByRecipeId(recipeId);
    }

    public List<Ingredient> createIngredientsFromDto(List<IngredientDto> ingredientDtos, Recipe recipe) {
        if (ingredientDtos == null || ingredientDtos.isEmpty()) {
            return List.of();
        }

        List<Ingredient> ingredients = ingredientDtos.stream()
                .map(dto -> IngredientMapper.toIngredient(dto, recipe))
                .collect(Collectors.toList());

        return ingredientRepository.saveAll(ingredients);
    }
}
