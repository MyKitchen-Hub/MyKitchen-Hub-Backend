package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingListEmailTemplatesTest {

    @Test
    void getShoppingListEmailPlainText_ShouldReturnFormattedPlainText() {
        User user = createTestUser();
        String shoppingList = "1. Flour - 500g\n2. Sugar - 200g\n3. Eggs - 3 pieces";

        String result = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello " + user.getUsername() + "! 🛒"));
        assertTrue(result.contains("Here is your freshly generated shopping list from MyKitchen Hub:"));
        assertTrue(result.contains(shoppingList));
        assertTrue(result.contains(
                "📎 A PDF version of this shopping list is attached to this email for easy printing and offline use."));
        assertTrue(result.contains("Happy cooking! 🍳"));
        assertTrue(result.contains("Best regards,"));
        assertTrue(result.contains("MyKitchen Hub Team 🚀"));
    }

    @Test
    void getShoppingListEmailPlainText_ShouldHandleNullUser() {
        String shoppingList = "Test shopping list";

        assertThrows(NullPointerException.class,
                () -> ShoppingListEmailTemplates.getShoppingListEmailPlainText(null, shoppingList));
    }

    @Test
    void getShoppingListEmailHtml_ShouldReturnFormattedHtml() {
        User user = createTestUser();
        String shoppingList = "1. Flour - 500g\n2. Sugar - 200g\n3. Eggs - 3 pieces";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingList);

        assertNotNull(result);
        assertTrue(result.contains("<!DOCTYPE html>"));
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("</html>"));
        assertTrue(result.contains("Hello <strong>" + user.getUsername() + "</strong> 👋"));
        assertTrue(result.contains("🛒 Your Shopping List"));
        assertTrue(result.contains("Here is your freshly generated shopping list:"));
        assertTrue(result.contains("<pre>" + shoppingList + "</pre>"));
        assertTrue(result.contains("Happy cooking! 🍳"));
        assertTrue(result.contains("📎 PDF Attachment"));
        assertTrue(
                result.contains("A PDF version of this shopping list is attached for easy printing and offline use."));
        assertTrue(result.contains("Best regards,<br>MyKitchen Hub Team 🚀"));
        assertTrue(result.contains("cid:logo"));
    }

    @Test
    void getShoppingListEmailHtml_ShouldHandleNullUser() {
        String shoppingList = "Test shopping list";

        assertThrows(NullPointerException.class,
                () -> ShoppingListEmailTemplates.getShoppingListEmailHtml(null, shoppingList));
    }

    @Test
    void getShoppingListEmailHtml_ShouldContainValidHtmlStructure() {
        User user = createTestUser();
        String shoppingList = "Test shopping list";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingList);

        assertTrue(result.contains("<head>"));
        assertTrue(result.contains("</head>"));
        assertTrue(result.contains("<body>"));
        assertTrue(result.contains("</body>"));
        assertTrue(result.contains("<style>"));
        assertTrue(result.contains("</style>"));
        assertTrue(result.contains("font-family: Arial, sans-serif"));
        assertTrue(result.contains("background-color: #FDE6DB"));
        assertTrue(result.contains("background-color: #EF4B33"));
    }

    @Test
    void getShoppingListEmailHtml_ShouldContainCssClasses() {
        User user = createTestUser();
        String shoppingList = "Test shopping list";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingList);

        assertTrue(result.contains("class=\"container\""));
        assertTrue(result.contains("class=\"header\""));
        assertTrue(result.contains("class=\"logo\""));
        assertTrue(result.contains("class=\"content\""));
        assertTrue(result.contains("class=\"shopping-list\""));
        assertTrue(result.contains("class=\"pdf-info\""));
        assertTrue(result.contains("class=\"footer\""));
        assertTrue(result.contains("class=\"greeting\""));
        assertTrue(result.contains("class=\"intro\""));
        assertTrue(result.contains("class=\"cooking-note\""));
    }

    @Test
    void getShoppingListEmailHtml_ShouldContainPreTagForShoppingList() {
        User user = createTestUser();
        String shoppingList = "1. Flour - 500g\n2. Sugar - 200g";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingList);

        assertTrue(result.contains("<pre>"));
        assertTrue(result.contains("</pre>"));
        assertTrue(result.contains("white-space: pre-wrap"));
        assertTrue(result.contains("word-wrap: break-word"));
        assertTrue(result.contains("font-family: 'Courier New', monospace"));
    }

    @Test
    void getShoppingListEmailPlainText_ShouldHandleEmptyShoppingList() {
        User user = createTestUser();
        String emptyShoppingList = "";

        String result = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, emptyShoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello " + user.getUsername() + "! 🛒"));
        assertTrue(result.contains("Here is your freshly generated shopping list from MyKitchen Hub:"));
        assertTrue(result.contains("Happy cooking! 🍳"));
    }

    @Test
    void getShoppingListEmailHtml_ShouldHandleEmptyShoppingList() {
        User user = createTestUser();
        String emptyShoppingList = "";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, emptyShoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello <strong>" + user.getUsername() + "</strong> 👋"));
        assertTrue(result.contains("Here is your freshly generated shopping list:"));
        assertTrue(result.contains("<pre></pre>"));
        assertTrue(result.contains("Happy cooking! 🍳"));
    }

    @Test
    void getShoppingListEmailPlainText_ShouldHandleSpecialCharactersInUsername() {
        User user = createTestUser();
        user.setUsername("Test@User#123");
        String shoppingList = "Test shopping list";

        String result = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello Test@User#123! 🛒"));
    }

    @Test
    void getShoppingListEmailHtml_ShouldHandleSpecialCharactersInUsername() {
        User user = createTestUser();
        user.setUsername("Test@User#123");
        String shoppingList = "Test shopping list";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello <strong>Test@User#123</strong> 👋"));
    }

    @Test
    void getShoppingListEmailPlainText_ShouldHandleEmptyUsername() {
        User user = createTestUser();
        user.setUsername("");
        String shoppingList = "Test shopping list";

        String result = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello ! 🛒"));
    }

    @Test
    void getShoppingListEmailHtml_ShouldHandleEmptyUsername() {
        User user = createTestUser();
        user.setUsername("");
        String shoppingList = "Test shopping list";

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingList);

        assertNotNull(result);
        assertTrue(result.contains("Hello <strong></strong> 👋"));
    }

    @Test
    void getShoppingListEmailPlainText_ShouldHandleLongShoppingList() {
        User user = createTestUser();
        StringBuilder longShoppingList = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            longShoppingList.append(i).append(". Item ").append(i).append(" - ").append(i).append("g\n");
        }

        String result = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, longShoppingList.toString());

        assertNotNull(result);
        assertTrue(result.contains("Hello " + user.getUsername() + "! 🛒"));
        assertTrue(result.contains("1. Item 1 - 1g"));
        assertTrue(result.contains("100. Item 100 - 100g"));
        assertTrue(result.contains("Happy cooking! 🍳"));
    }

    @Test
    void getShoppingListEmailHtml_ShouldHandleLongShoppingList() {
        User user = createTestUser();
        StringBuilder longShoppingList = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            longShoppingList.append(i).append(". Item ").append(i).append(" - ").append(i).append("g\n");
        }

        String result = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, longShoppingList.toString());

        assertNotNull(result);
        assertTrue(result.contains("Hello <strong>" + user.getUsername() + "</strong> 👋"));
        assertTrue(result.contains("<pre>"));
        assertTrue(result.contains("1. Item 1 - 1g"));
        assertTrue(result.contains("100. Item 100 - 100g"));
        assertTrue(result.contains("</pre>"));
        assertTrue(result.contains("Happy cooking! 🍳"));
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        return user;
    }
}
