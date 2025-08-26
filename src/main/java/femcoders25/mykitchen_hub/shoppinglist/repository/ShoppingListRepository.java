package femcoders25.mykitchen_hub.shoppinglist.repository;

import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
}
