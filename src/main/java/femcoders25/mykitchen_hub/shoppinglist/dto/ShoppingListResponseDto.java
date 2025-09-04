package femcoders25.mykitchen_hub.shoppinglist.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShoppingListResponseDto(
        Long id,
        String name,
        String generatedBy,
        List<ListItemResponseDto> listItems,
        String generatedFromRecipe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
