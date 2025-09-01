package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @InjectMocks
    private PdfService pdfService;

    @Test
    void generateShoppingListPdf_ShouldGenerateValidPdf() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> listItems = createTestListItems();
        shoppingList.setListItems(listItems);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        String pdfHeader = new String(pdfBytes, 0, Math.min(4, pdfBytes.length));
        assertTrue(pdfBytes.length > 100, "PDF should be larger than 100 bytes");
    }

    @Test
    void generateShoppingListPdf_ShouldHandleEmptyList() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setListItems(new ArrayList<>());

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        return user;
    }

    private ShoppingList createTestShoppingList(User user) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(1L);
        shoppingList.setName("Test Shopping List");
        shoppingList.setGeneratedBy(user);
        shoppingList.setGeneratedFromRecipe("Test Recipe 1, Test Recipe 2");
        shoppingList.setCreatedAt(LocalDateTime.now());
        return shoppingList;
    }

    private List<ListItem> createTestListItems() {
        List<ListItem> items = new ArrayList<>();
        
        ListItem item1 = new ListItem();
        item1.setId(1L);
        item1.setName("Flour");
        item1.setAmount(500.0);
        item1.setUnit("g");
        items.add(item1);
        
        ListItem item2 = new ListItem();
        item2.setId(2L);
        item2.setName("Sugar");
        item2.setAmount(200.0);
        item2.setUnit("g");
        items.add(item2);
        
        ListItem item3 = new ListItem();
        item3.setId(3L);
        item3.setName("Eggs");
        item3.setAmount(3.0);
        item3.setUnit("pieces");
        items.add(item3);
        
        return items;
    }
}
