package femcoders25.mykitchen_hub.shoppinglist.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListCreateDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListResponseDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListUpdateDto;
import femcoders25.mykitchen_hub.shoppinglist.service.ShoppingListService;
import femcoders25.mykitchen_hub.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor

public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShoppingListResponseDto>> createShoppingList(
            @Valid @RequestBody ShoppingListCreateDto createDto,
            @AuthenticationPrincipal User user) {

        log.info("Creating shopping list for user: {}", user.getUsername());
        ShoppingListResponseDto response = shoppingListService.createShoppingList(createDto, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shopping list created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShoppingListResponseDto>> getShoppingList(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        ShoppingListResponseDto response = shoppingListService.getShoppingListById(id, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShoppingListResponseDto>>> getUserShoppingLists(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String name) {

        List<ShoppingListResponseDto> response;
        if (name != null && !name.trim().isEmpty()) {
            response = shoppingListService.searchUserShoppingLists(user, name);
        } else {
            response = shoppingListService.getUserShoppingLists(user);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShoppingListResponseDto>> updateShoppingList(
            @PathVariable Long id,
            @Valid @RequestBody ShoppingListUpdateDto updateDto,
            @AuthenticationPrincipal User user) {

        ShoppingListResponseDto response = shoppingListService.updateShoppingList(id, updateDto, user);
        return ResponseEntity.ok(ApiResponse.success("Shopping list updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteShoppingList(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        ApiResponse<String> response = shoppingListService.deleteShoppingList(id, user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{listId}/items/{itemId}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleItemChecked(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user) {

        ApiResponse<String> response = shoppingListService.toggleItemChecked(listId, itemId, user);
        return ResponseEntity.ok(response);
    }
}

