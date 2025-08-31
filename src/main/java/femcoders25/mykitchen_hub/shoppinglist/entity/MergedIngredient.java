package femcoders25.mykitchen_hub.shoppinglist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergedIngredient {
    private String name;
    private String unit;
    private double totalAmount;

    public void addAmount(double amount) {
        this.totalAmount += amount;
    }
}
