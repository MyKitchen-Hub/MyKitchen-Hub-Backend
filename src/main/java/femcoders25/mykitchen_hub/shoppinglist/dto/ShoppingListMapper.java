package femcoders25.mykitchen_hub.shoppinglist.dto;

import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShoppingListMapper {

    public ShoppingListResponseDto toResponseDto(ShoppingList shoppingList) {
        if (shoppingList == null) {
            return null;
        }

        List<ListItemResponseDto> listItems = null;
        if (shoppingList.getListItems() != null) {
            listItems = shoppingList.getListItems().stream()
                    .map(this::toListItemResponseDto)
                    .collect(Collectors.toList());
        }

        return new ShoppingListResponseDto(
                shoppingList.getId(),
                shoppingList.getName(),
                shoppingList.getGeneratedBy() != null ? shoppingList.getGeneratedBy().getUsername() : null,
                listItems,
                shoppingList.getGeneratedFromRecipe(),
                shoppingList.getCreatedAt(),
                shoppingList.getUpdatedAt());
    }

    public ListItemResponseDto toListItemResponseDto(ListItem listItem) {
        if (listItem == null) {
            return null;
        }

        return new ListItemResponseDto(
                listItem.getId(),
                listItem.getName(),
                listItem.getAmount(),
                listItem.getUnit(),
                listItem.getIsChecked());
    }

    public List<ShoppingListResponseDto> toResponseDtoList(List<ShoppingList> shoppingLists) {
        if (shoppingLists == null) {
            return null;
        }

        return shoppingLists.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}

