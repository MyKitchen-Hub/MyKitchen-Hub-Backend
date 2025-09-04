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
        assertTrue(pdfBytes.length > 100, "PDF should be larger than 100 bytes");

        String pdfHeader = new String(pdfBytes, 0, Math.min(4, pdfBytes.length));
        assertTrue(pdfHeader.startsWith("%PDF"), "Generated content should be a valid PDF");
    }

    @Test
    void generateShoppingListPdf_ShouldHandleEmptyList() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setListItems(new ArrayList<>());

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertTrue(pdfBytes.length > 100, "PDF should be larger than 100 bytes even with empty list");
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNullListItems() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setListItems(null);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNullGeneratedFromRecipe() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setGeneratedFromRecipe(null);
        shoppingList.setListItems(createTestListItems());

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleEmptyGeneratedFromRecipe() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setGeneratedFromRecipe("");
        shoppingList.setListItems(createTestListItems());

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNullShoppingListName() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setName(null);
        shoppingList.setListItems(createTestListItems());

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleEmptyShoppingListName() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setName("");
        shoppingList.setListItems(createTestListItems());

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNullUser() {
        ShoppingList shoppingList = createTestShoppingList(null);
        shoppingList.setListItems(createTestListItems());

        assertThrows(NullPointerException.class, () -> pdfService.generateShoppingListPdf(shoppingList, null));
    }

    @Test
    void generateShoppingListPdf_ShouldHandleLargeList() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> largeList = createLargeListItems(50);
        shoppingList.setListItems(largeList);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertTrue(pdfBytes.length > 1000, "PDF should be larger for a large list");
    }

    @Test
    void generateShoppingListPdf_ShouldHandleSpecialCharactersInItemNames() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> itemsWithSpecialChars = createItemsWithSpecialCharacters();
        shoppingList.setListItems(itemsWithSpecialChars);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleZeroAmounts() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> itemsWithZeroAmounts = createItemsWithZeroAmounts();
        shoppingList.setListItems(itemsWithZeroAmounts);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNegativeAmounts() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> itemsWithNegativeAmounts = createItemsWithNegativeAmounts();
        shoppingList.setListItems(itemsWithNegativeAmounts);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNullItemNames() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> itemsWithNullNames = createItemsWithNullNames();
        shoppingList.setListItems(itemsWithNullNames);

        byte[] pdfBytes = pdfService.generateShoppingListPdf(shoppingList, user);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generateShoppingListPdf_ShouldHandleNullUnits() {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> itemsWithNullUnits = createItemsWithNullUnits();
        shoppingList.setListItems(itemsWithNullUnits);

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

    private List<ListItem> createLargeListItems(int count) {
        List<ListItem> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ListItem item = new ListItem();
            item.setId((long) i);
            item.setName("Item " + i);
            item.setAmount((double) i);
            item.setUnit("g");
            items.add(item);
        }
        return items;
    }

    private List<ListItem> createItemsWithSpecialCharacters() {
        List<ListItem> items = new ArrayList<>();

        ListItem item1 = new ListItem();
        item1.setId(1L);
        item1.setName("Café & Co. (Special)");
        item1.setAmount(1.5);
        item1.setUnit("kg");
        items.add(item1);

        ListItem item2 = new ListItem();
        item2.setId(2L);
        item2.setName("Tomato's & Onion's");
        item2.setAmount(2.0);
        item2.setUnit("pieces");
        items.add(item2);

        ListItem item3 = new ListItem();
        item3.setId(3L);
        item3.setName("Spice-Mix #1");
        item3.setAmount(0.5);
        item3.setUnit("tsp");
        items.add(item3);

        return items;
    }

    private List<ListItem> createItemsWithZeroAmounts() {
        List<ListItem> items = new ArrayList<>();

        ListItem item1 = new ListItem();
        item1.setId(1L);
        item1.setName("Zero Amount Item");
        item1.setAmount(0.0);
        item1.setUnit("g");
        items.add(item1);

        return items;
    }

    private List<ListItem> createItemsWithNegativeAmounts() {
        List<ListItem> items = new ArrayList<>();

        ListItem item1 = new ListItem();
        item1.setId(1L);
        item1.setName("Negative Amount Item");
        item1.setAmount(-5.0);
        item1.setUnit("g");
        items.add(item1);

        return items;
    }

    private List<ListItem> createItemsWithNullNames() {
        List<ListItem> items = new ArrayList<>();

        ListItem item1 = new ListItem();
        item1.setId(1L);
        item1.setName(null);
        item1.setAmount(1.0);
        item1.setUnit("g");
        items.add(item1);

        return items;
    }

    private List<ListItem> createItemsWithNullUnits() {
        List<ListItem> items = new ArrayList<>();

        ListItem item1 = new ListItem();
        item1.setId(1L);
        item1.setName("Item without unit");
        item1.setAmount(1.0);
        item1.setUnit(null);
        items.add(item1);

        return items;
    }
}
