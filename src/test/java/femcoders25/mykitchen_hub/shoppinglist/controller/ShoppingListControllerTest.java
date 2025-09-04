package femcoders25.mykitchen_hub.shoppinglist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListCreateDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListResponseDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListUpdateDto;
import femcoders25.mykitchen_hub.shoppinglist.service.ShoppingListService;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListControllerTest {

    @Mock
    private ShoppingListService shoppingListService;

    @InjectMocks
    private ShoppingListController shoppingListController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ShoppingListResponseDto testResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(shoppingListController).build();
        objectMapper = new ObjectMapper();

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testResponse = new ShoppingListResponseDto(
                1L,
                "Test Shopping List",
                "testuser",
                null,
                "Test Recipe",
                null,
                null);
    }

    @Test
    void createShoppingList_Success() throws Exception {
        ShoppingListCreateDto createDto = new ShoppingListCreateDto(
                "My Shopping List",
                Arrays.asList(1L, 2L));

        when(shoppingListService.createShoppingList(any(ShoppingListCreateDto.class), any(User.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/shopping-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Shopping list created successfully"));
    }

    @Test
    void getShoppingList_Success() throws Exception {
        Long listId = 1L;
        when(shoppingListService.getShoppingListById(eq(listId), any(User.class)))
                .thenReturn(testResponse);

        mockMvc.perform(get("/api/shopping-lists/{id}", listId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUserShoppingLists_Success() throws Exception {
        List<ShoppingListResponseDto> responseList = Collections.singletonList(testResponse);
        when(shoppingListService.getUserShoppingLists(any(User.class)))
                .thenReturn(responseList);

        mockMvc.perform(get("/api/shopping-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void searchUserShoppingLists_Success() throws Exception {
        List<ShoppingListResponseDto> responseList = Collections.singletonList(testResponse);
        when(shoppingListService.searchUserShoppingLists(any(User.class), eq("test")))
                .thenReturn(responseList);

        mockMvc.perform(get("/api/shopping-lists")
                        .param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateShoppingList_Success() throws Exception {
        Long listId = 1L;
        ShoppingListUpdateDto updateDto = new ShoppingListUpdateDto("Updated Name", List.of(1L, 2L));

        when(shoppingListService.updateShoppingList(eq(listId), any(ShoppingListUpdateDto.class),
                any(User.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/api/shopping-lists/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Shopping list updated successfully"));
    }

    @Test
    void deleteShoppingList_Success() throws Exception {
        Long listId = 1L;
        ApiResponse<String> deleteResponse = ApiResponse.success("Shopping list deleted successfully");
        when(shoppingListService.deleteShoppingList(eq(listId), any(User.class)))
                .thenReturn(deleteResponse);

        mockMvc.perform(delete("/api/shopping-lists/{id}", listId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void toggleItemChecked_Success() throws Exception {
        Long listId = 1L;
        Long itemId = 1L;
        ApiResponse<String> toggleResponse = ApiResponse.success("Item status updated successfully");
        when(shoppingListService.toggleItemChecked(eq(listId), eq(itemId), any(User.class)))
                .thenReturn(toggleResponse);

        mockMvc.perform(patch("/api/shopping-lists/{listId}/items/{itemId}/toggle", listId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
