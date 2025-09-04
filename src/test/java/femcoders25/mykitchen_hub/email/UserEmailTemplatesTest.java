package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailTemplatesTest {

    @Test
    void getUserWelcomeEmailPlainText_ShouldReturnFormattedPlainText() {
        User user = createTestUser();

        String result = UserEmailTemplates.getUserWelcomeEmailPlainText(user);

        assertNotNull(result);
        assertTrue(result.contains("Hello " + user.getUsername() + "! 👋"));
        assertTrue(result.contains("Welcome to MyKitchen Hub! 🎉"));
        assertTrue(result.contains("We're excited to have you join our community of food lovers"));
        assertTrue(result.contains("Best regards,"));
        assertTrue(result.contains("MyKitchen Hub Team 🚀"));
    }

    @Test
    void getUserWelcomeEmailPlainText_ShouldHandleNullUser() {
        assertThrows(NullPointerException.class, () -> UserEmailTemplates.getUserWelcomeEmailPlainText(null));
    }

    @Test
    void getUserWelcomeEmailHtml_ShouldReturnFormattedHtml() {
        User user = createTestUser();

        String result = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        assertNotNull(result);
        assertTrue(result.contains("<!DOCTYPE html>"));
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("</html>"));
        assertTrue(result.contains("Hello <strong>" + user.getUsername() + "</strong> 👋"));
        assertTrue(result.contains("🎉 Welcome to MyKitchen Hub!"));
        assertTrue(result.contains("We're thrilled to have you join our community of food lovers!"));
        assertTrue(result.contains("Explore delicious recipes"));
        assertTrue(result.contains("Create your own and share them"));
        assertTrue(result.contains("Generate smart shopping lists"));
        assertTrue(result.contains("Log In Now"));
        assertTrue(result.contains("Best regards,<br>MyKitchen Hub Team 🚀"));
        assertTrue(result.contains("cid:logo"));
    }

    @Test
    void getUserWelcomeEmailHtml_ShouldHandleNullUser() {
        assertThrows(NullPointerException.class, () -> UserEmailTemplates.getUserWelcomeEmailHtml(null));
    }

    @Test
    void getUserWelcomeEmailHtml_ShouldContainValidHtmlStructure() {
        User user = createTestUser();

        String result = UserEmailTemplates.getUserWelcomeEmailHtml(user);

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
    void getUserWelcomeEmailHtml_ShouldContainCssClasses() {
        User user = createTestUser();

        String result = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        assertTrue(result.contains("class=\"container\""));
        assertTrue(result.contains("class=\"header\""));
        assertTrue(result.contains("class=\"logo\""));
        assertTrue(result.contains("class=\"content\""));
        assertTrue(result.contains("class=\"button\""));
        assertTrue(result.contains("class=\"footer\""));
        assertTrue(result.contains("class=\"greeting\""));
        assertTrue(result.contains("class=\"intro\""));
        assertTrue(result.contains("class=\"features\""));
        assertTrue(result.contains("class=\"button-container\""));
    }

    @Test
    void getUserWelcomeEmailHtml_ShouldContainListItems() {
        User user = createTestUser();

        String result = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("</ul>"));
        assertTrue(result.contains("<li>"));
        assertTrue(result.contains("</li>"));
    }

    @Test
    void getUserWelcomeEmailPlainText_ShouldHandleSpecialCharactersInUsername() {
        User user = createTestUser();
        user.setUsername("Test@User#123");

        String result = UserEmailTemplates.getUserWelcomeEmailPlainText(user);

        assertNotNull(result);
        assertTrue(result.contains("Hello Test@User#123! 👋"));
    }

    @Test
    void getUserWelcomeEmailHtml_ShouldHandleSpecialCharactersInUsername() {
        User user = createTestUser();
        user.setUsername("Test@User#123");

        String result = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        assertNotNull(result);
        assertTrue(result.contains("Hello <strong>Test@User#123</strong> 👋"));
    }

    @Test
    void getUserWelcomeEmailPlainText_ShouldHandleEmptyUsername() {
        User user = createTestUser();
        user.setUsername("");

        String result = UserEmailTemplates.getUserWelcomeEmailPlainText(user);

        assertNotNull(result);
        assertTrue(result.contains("Hello ! 👋"));
    }

    @Test
    void getUserWelcomeEmailHtml_ShouldHandleEmptyUsername() {
        User user = createTestUser();
        user.setUsername("");

        String result = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        assertNotNull(result);
        assertTrue(result.contains("Hello <strong></strong> 👋"));
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
