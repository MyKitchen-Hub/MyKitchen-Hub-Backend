package femcoders25.mykitchen_hub.shoppinglist.repository;

import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findByGeneratedByOrderByCreatedAtDesc(User user);

    List<ShoppingList> findByGeneratedByAndNameContainingIgnoreCaseOrderByCreatedAtDesc(User user, String name);
}
