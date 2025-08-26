package femcoders25.mykitchen_hub.shoppinglist.repository;

import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {
}
