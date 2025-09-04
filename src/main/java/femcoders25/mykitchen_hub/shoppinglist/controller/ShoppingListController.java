package femcoders25.mykitchen_hub.shoppinglist.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListCreateDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListResponseDto;
import femcoders25.mykitchen_hub.shoppinglist.dto.ShoppingListUpdateDto;
import femcoders25.mykitchen_hub.shoppinglist.service.ShoppingListService;
import femcoders25.mykitchen_hub.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Shopping List Management", description = "Shopping list management endpoints for creating, updating, and managing shopping lists")
public class ShoppingListController {

        private final ShoppingListService shoppingListService;

        @Operation(summary = "Create shopping list", description = "Creates a new shopping list for the authenticated user. Name must be 1-100 characters, recipeIds is a list of recipe IDs to include.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Shopping list created successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Shopping list creation data", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShoppingListCreateDto.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Create shopping list", value = "{\"name\":\"Weekly Groceries\",\"recipeIds\":[1,2,3]}")))
        @PostMapping
        public ResponseEntity<ApiResponse<ShoppingListResponseDto>> createShoppingList(
                        @Valid @RequestBody ShoppingListCreateDto createDto,
                        @AuthenticationPrincipal User user) {

                log.info("Creating shopping list for user: {}", user.getUsername());
                ShoppingListResponseDto response = shoppingListService.createShoppingList(createDto, user);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Shopping list created successfully", response));
        }

        @Operation(summary = "Get shopping list by ID", description = "Retrieves a specific shopping list by ID for the authenticated user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shopping list retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shopping list not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<ShoppingListResponseDto>> getShoppingList(
                        @Parameter(description = "Shopping list ID") @PathVariable Long id,
                        @AuthenticationPrincipal User user) {

                ShoppingListResponseDto response = shoppingListService.getShoppingListById(id, user);
                return ResponseEntity.ok(ApiResponse.<ShoppingListResponseDto>success(response));
        }

        @Operation(summary = "Get user shopping lists", description = "Retrieves all shopping lists for the authenticated user, optionally filtered by name")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shopping lists retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @GetMapping
        public ResponseEntity<ApiResponse<List<ShoppingListResponseDto>>> getUserShoppingLists(
                        @AuthenticationPrincipal User user,
                        @Parameter(description = "Optional name filter for shopping lists") @RequestParam(required = false) String name) {

                List<ShoppingListResponseDto> response;
                if (name != null && !name.trim().isEmpty()) {
                        response = shoppingListService.searchUserShoppingLists(user, name);
                } else {
                        response = shoppingListService.getUserShoppingLists(user);
                }

                return ResponseEntity.ok(ApiResponse.<List<ShoppingListResponseDto>>success(response));
        }

        @Operation(summary = "Update shopping list", description = "Updates an existing shopping list for the authenticated user. Name must be 1-100 characters, recipeIds is a list of recipe IDs to include.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shopping list updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shopping list not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Shopping list update data", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShoppingListUpdateDto.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Update shopping list", value = "{\"name\":\"Updated Weekly Groceries\",\"recipeIds\":[1,2,3,4]}")))
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<ShoppingListResponseDto>> updateShoppingList(
                        @Parameter(description = "Shopping list ID") @PathVariable Long id,
                        @Valid @RequestBody ShoppingListUpdateDto updateDto,
                        @AuthenticationPrincipal User user) {

                ShoppingListResponseDto response = shoppingListService.updateShoppingList(id, updateDto, user);
                return ResponseEntity.ok(ApiResponse.success("Shopping list updated successfully", response));
        }

        @Operation(summary = "Delete shopping list", description = "Deletes a shopping list for the authenticated user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shopping list deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shopping list not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<String>> deleteShoppingList(
                        @Parameter(description = "Shopping list ID") @PathVariable Long id,
                        @AuthenticationPrincipal User user) {

                ApiResponse<String> response = shoppingListService.deleteShoppingList(id, user);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Toggle item checked status", description = "Toggles the checked status of an item in a shopping list")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item status toggled successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shopping list or item not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @PatchMapping("/{listId}/items/{itemId}/toggle")
        public ResponseEntity<ApiResponse<String>> toggleItemChecked(
                        @Parameter(description = "Shopping list ID") @PathVariable Long listId,
                        @Parameter(description = "Item ID") @PathVariable Long itemId,
                        @AuthenticationPrincipal User user) {

                ApiResponse<String> response = shoppingListService.toggleItemChecked(listId, itemId, user);
                return ResponseEntity.ok(response);
        }
}
