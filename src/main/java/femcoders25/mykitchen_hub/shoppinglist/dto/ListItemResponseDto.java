package femcoders25.mykitchen_hub.shoppinglist.dto;

public record ListItemResponseDto(
        Long id,
        String name,
        Double amount,
        String unit,
        Boolean isChecked) {
}
