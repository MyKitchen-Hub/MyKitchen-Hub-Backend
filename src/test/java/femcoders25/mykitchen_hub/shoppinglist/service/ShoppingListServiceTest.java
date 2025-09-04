package femcoders25.mykitchen_hub.shoppinglist.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
import femcoders25.mykitchen_hub.ingredient.entity.Ingredient;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListCreateDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListResponseDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListUpdateDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListMapper;
import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.shoppinglist.repository.ListItemRepository;
import femcoders25.mykitchen_hub.shoppinglist.repository.ShoppingListRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.email.PdfService;
import femcoders25.mykitchen_hub.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ListItemRepository listItemRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ShoppingListMapper shoppingListMapper;

    @Mock
    private PdfService pdfService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ShoppingListService shoppingListService;

    private User testUser;
    private Recipe testRecipe1;
    private Recipe testRecipe2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRecipe1 = new Recipe();
        testRecipe1.setId(1L);
        testRecipe1.setTitle("Recipe 1");

        testRecipe2 = new Recipe();
        testRecipe2.setId(2L);
        testRecipe2.setTitle("Recipe 2");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Flour");
        ingredient1.setAmount(200.0);
        ingredient1.setUnit("g");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Sugar");
        ingredient2.setAmount(100.0);
        ingredient2.setUnit("g");

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setName("Flour");
        ingredient3.setAmount(300.0);
        ingredient3.setUnit("g");

        testRecipe1.setIngredients(Arrays.asList(ingredient1, ingredient2));
        testRecipe2.setIngredients(List.of(ingredient3));

        lenient().when(pdfService.generateShoppingListPdf(any(), any())).thenReturn(new byte[0]);
    }

    @Test
    void createShoppingList_Success() {
        ShoppingListCreateDto createDto = new ShoppingListCreateDto(
                "My Shopping List",
                Arrays.asList(1L, 2L));

        when(recipeRepository.findAllById(any())).thenReturn(Arrays.asList(testRecipe1, testRecipe2));
        ShoppingList savedShoppingList = new ShoppingList();
        savedShoppingList.setName("My Shopping List");
        when(shoppingListRepository.save(any())).thenReturn(savedShoppingList);
        when(listItemRepository.saveAll(any())).thenReturn(List.of(new ListItem()));

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(any())).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.createShoppingList(createDto, testUser);

        assertNotNull(result);
        verify(recipeRepository).findAllById(Arrays.asList(1L, 2L));
        verify(shoppingListRepository).save(any());
        verify(listItemRepository).saveAll(any());
    }

    @Test
    void createShoppingList_RecipeNotFound() {
        ShoppingListCreateDto createDto = new ShoppingListCreateDto(
                "My Shopping List",
                Arrays.asList(1L, 999L));

        when(recipeRepository.findAllById(any())).thenReturn(Collections.singletonList(testRecipe1));

        assertThrows(ResourceNotFoundException.class,
                () -> shoppingListService.createShoppingList(createDto, testUser));
    }

    @Test
    void getShoppingListById_Success() {
        Long listId = 1L;
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(shoppingList)).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.getShoppingListById(listId, testUser);

        assertNotNull(result);
        verify(shoppingListRepository).findById(listId);
    }

    @Test
    void getShoppingListById_NotFound() {
        Long listId = 999L;
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shoppingListService.getShoppingListById(listId, testUser));
    }

    @Test
    void getShoppingListById_Unauthorized() {
        Long listId = 1L;
        User otherUser = new User();
        otherUser.setId(999L);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(otherUser);

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));

        assertThrows(UnauthorizedOperationException.class,
                () -> shoppingListService.getShoppingListById(listId, testUser));
    }

    @Test
    void updateShoppingList_Success() {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 2L));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);
        shoppingList.setListItems(new ArrayList<>());

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any())).thenReturn(shoppingList);
        when(recipeRepository.findAllById(any())).thenReturn(List.of(new Recipe(), new Recipe()));
        when(listItemRepository.saveAll(any())).thenReturn(new ArrayList<>());

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(shoppingList)).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.updateShoppingList(listId, updateDto, testUser);

        assertNotNull(result);
        verify(shoppingListRepository).save(shoppingList);
        assertEquals("Updated Name", shoppingList.getName());
        verify(recipeRepository).findAllById(updateDto.recipeIds());
    }

    @Test
    void updateShoppingList_WithNewRecipes_Success() {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 2L));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);
        shoppingList.setListItems(new ArrayList<>());

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);
        recipe1.setTitle("Recipe 1");

        Recipe recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setTitle("Recipe 2");

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any())).thenReturn(shoppingList);
        when(recipeRepository.findAllById(any())).thenReturn(List.of(recipe1, recipe2));
        when(listItemRepository.saveAll(any())).thenReturn(new ArrayList<>());

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(shoppingList)).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.updateShoppingList(listId, updateDto, testUser);

        assertNotNull(result);
        verify(shoppingListRepository).save(shoppingList);
        assertEquals("Updated Name", shoppingList.getName());
        verify(recipeRepository).findAllById(updateDto.recipeIds());
        verify(listItemRepository).deleteAll(shoppingList.getListItems());
        verify(listItemRepository).saveAll(any());

        assertEquals("Recipe 1, Recipe 2", shoppingList.getGeneratedFromRecipe());
    }

    @Test
    void updateShoppingList_NotFound() {
        Long listId = 999L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 2L));

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shoppingListService.updateShoppingList(listId, updateDto, testUser));

        verify(shoppingListRepository).findById(listId);
        verify(shoppingListRepository, never()).save(any());
    }

    @Test
    void updateShoppingList_Unauthorized() {
        Long listId = 1L;
        User otherUser = new User();
        otherUser.setId(999L);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(otherUser);

        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 2L));

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));

        assertThrows(UnauthorizedOperationException.class,
                () -> shoppingListService.updateShoppingList(listId, updateDto, testUser));

        verify(shoppingListRepository).findById(listId);
        verify(shoppingListRepository, never()).save(any());
    }

    @Test
    void updateShoppingList_WithNullRecipeIds_Success() {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", null);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);
        shoppingList.setListItems(new ArrayList<>());

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any())).thenReturn(shoppingList);

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(shoppingList)).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.updateShoppingList(listId, updateDto, testUser);

        assertNotNull(result);
        verify(shoppingListRepository).save(shoppingList);
        assertEquals("Updated Name", shoppingList.getName());
        verify(recipeRepository, never()).findAllById(any());
        verify(listItemRepository, never()).deleteAll(any());
        verify(listItemRepository, never()).saveAll(any());
    }

    @Test
    void updateShoppingList_WithEmptyRecipeIds_Success() {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of());

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);
        shoppingList.setListItems(new ArrayList<>());

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any())).thenReturn(shoppingList);

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(shoppingList)).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.updateShoppingList(listId, updateDto, testUser);

        assertNotNull(result);
        verify(shoppingListRepository).save(shoppingList);
        assertEquals("Updated Name", shoppingList.getName());
        verify(recipeRepository, never()).findAllById(any());
        verify(listItemRepository, never()).deleteAll(any());
        verify(listItemRepository, never()).saveAll(any());
    }

    @Test
    void updateShoppingList_WithNewRecipes_SomeRecipesNotFound() {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 999L));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);
        shoppingList.setListItems(new ArrayList<>());

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(recipeRepository.findAllById(any())).thenReturn(Collections.singletonList(testRecipe1));

        assertThrows(ResourceNotFoundException.class,
                () -> shoppingListService.updateShoppingList(listId, updateDto, testUser));

        verify(shoppingListRepository).findById(listId);
        verify(recipeRepository).findAllById(updateDto.recipeIds());
        verify(shoppingListRepository, never()).save(any());
    }

    @Test
    void updateShoppingList_WithNewRecipes_WithNullListItems() {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 2L));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(listId);
        shoppingList.setGeneratedBy(testUser);
        shoppingList.setListItems(null);

        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);
        recipe1.setTitle("Recipe 1");

        Recipe recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setTitle("Recipe 2");

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any())).thenReturn(shoppingList);
        when(recipeRepository.findAllById(any())).thenReturn(List.of(recipe1, recipe2));
        when(listItemRepository.saveAll(any())).thenReturn(new ArrayList<>());

        ShoppingListResponseDto expectedResponse = new ShoppingListResponseDto(
                1L, "Test", "user", null, "recipe", null, null);
        when(shoppingListMapper.toResponseDto(shoppingList)).thenReturn(expectedResponse);

        ShoppingListResponseDto result = shoppingListService.updateShoppingList(listId, updateDto, testUser);

        assertNotNull(result);
        verify(shoppingListRepository).save(shoppingList);
        assertEquals("Updated Name", shoppingList.getName());
        verify(recipeRepository).findAllById(updateDto.recipeIds());
        verify(listItemRepository, never()).deleteAll(any());
        verify(listItemRepository).saveAll(any());
        assertEquals("Recipe 1, Recipe 2", shoppingList.getGeneratedFromRecipe());
    }
}
