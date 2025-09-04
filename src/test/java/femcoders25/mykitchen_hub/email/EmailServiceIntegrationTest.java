package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    @MockitoBean
    private JavaMailSender mailSender;

    @MockitoBean
    private MimeMessage mimeMessage;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendUserWelcomeEmail_ShouldSendEmailSuccessfully() throws MessagingException {
        User user = createTestUser();
        String subject = "Welcome to MyKitchen Hub!";
        String plainText = UserEmailTemplates.getUserWelcomeEmailPlainText(user);
        String htmlContent = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        assertDoesNotThrow(() -> emailService.sendUserWelcomeEmail(user.getEmail(), subject, plainText, htmlContent));
    }

    @Test
    void sendShoppingListCreatedEmail_ShouldSendEmailSuccessfully() throws MessagingException {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        String subject = "Your Shopping List from MyKitchen Hub";
        String shoppingListText = formatShoppingListText(shoppingList);
        String plainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String htmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        assertDoesNotThrow(
                () -> emailService.sendShoppingListCreatedEmail(user.getEmail(), subject, plainText, htmlContent));
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldSendEmailWithPdfSuccessfully() throws MessagingException {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setListItems(createTestListItems());

        String subject = "Your Shopping List from MyKitchen Hub";
        String shoppingListText = formatShoppingListText(shoppingList);
        String plainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String htmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        byte[] pdfContent = pdfService.generateShoppingListPdf(shoppingList, user);
        String pdfFileName = "shopping_list_" + shoppingList.getId() + ".pdf";

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmailWithPdf(
                user.getEmail(), subject, plainText, htmlContent, pdfContent, pdfFileName));
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldHandleEmptyPdf() throws MessagingException {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);

        String subject = "Your Shopping List from MyKitchen Hub";
        String shoppingListText = formatShoppingListText(shoppingList);
        String plainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String htmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        byte[] emptyPdf = new byte[0];
        String pdfFileName = "empty_shopping_list.pdf";

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmailWithPdf(
                user.getEmail(), subject, plainText, htmlContent, emptyPdf, pdfFileName));
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldHandleNullPdf() throws MessagingException {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);

        String subject = "Your Shopping List from MyKitchen Hub";
        String shoppingListText = formatShoppingListText(shoppingList);
        String plainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String htmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        String pdfFileName = "null_shopping_list.pdf";

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmailWithPdf(
                user.getEmail(), subject, plainText, htmlContent, null, pdfFileName));
    }

    @Test
    void emailService_ShouldWorkWithRealEmailTemplates() throws MessagingException {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        shoppingList.setListItems(createTestListItems());

        String welcomeSubject = "Welcome to MyKitchen Hub!";
        String welcomePlainText = UserEmailTemplates.getUserWelcomeEmailPlainText(user);
        String welcomeHtmlContent = UserEmailTemplates.getUserWelcomeEmailHtml(user);

        String shoppingListSubject = "Your Shopping List from MyKitchen Hub";
        String shoppingListText = formatShoppingListText(shoppingList);
        String shoppingListPlainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String shoppingListHtmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        byte[] pdfContent = pdfService.generateShoppingListPdf(shoppingList, user);
        String pdfFileName = "integration_test_shopping_list.pdf";

        assertDoesNotThrow(() -> emailService.sendUserWelcomeEmail(user.getEmail(), welcomeSubject, welcomePlainText,
                welcomeHtmlContent));

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmail(user.getEmail(), shoppingListSubject,
                shoppingListPlainText, shoppingListHtmlContent));

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmailWithPdf(
                user.getEmail(), shoppingListSubject, shoppingListPlainText, shoppingListHtmlContent, pdfContent,
                pdfFileName));
    }

    @Test
    void emailService_ShouldHandleSpecialCharactersInContent() throws MessagingException {
        User user = createTestUser();
        user.setUsername("Test@User#123");

        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> specialItems = createItemsWithSpecialCharacters();
        shoppingList.setListItems(specialItems);

        String subject = "Special Characters Test - Your Shopping List";
        String shoppingListText = formatShoppingListText(shoppingList);
        String plainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String htmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        byte[] pdfContent = pdfService.generateShoppingListPdf(shoppingList, user);
        String pdfFileName = "special_chars_shopping_list.pdf";

        assertDoesNotThrow(
                () -> emailService.sendShoppingListCreatedEmail(user.getEmail(), subject, plainText, htmlContent));

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmailWithPdf(
                user.getEmail(), subject, plainText, htmlContent, pdfContent, pdfFileName));
    }

    @Test
    void emailService_ShouldHandleLargeShoppingList() throws MessagingException {
        User user = createTestUser();
        ShoppingList shoppingList = createTestShoppingList(user);
        List<ListItem> largeList = createLargeListItems(100);
        shoppingList.setListItems(largeList);

        String subject = "Large Shopping List Test";
        String shoppingListText = formatShoppingListText(shoppingList);
        String plainText = ShoppingListEmailTemplates.getShoppingListEmailPlainText(user, shoppingListText);
        String htmlContent = ShoppingListEmailTemplates.getShoppingListEmailHtml(user, shoppingListText);

        byte[] pdfContent = pdfService.generateShoppingListPdf(shoppingList, user);
        String pdfFileName = "large_shopping_list.pdf";

        assertDoesNotThrow(
                () -> emailService.sendShoppingListCreatedEmail(user.getEmail(), subject, plainText, htmlContent));

        assertDoesNotThrow(() -> emailService.sendShoppingListCreatedEmailWithPdf(
                user.getEmail(), subject, plainText, htmlContent, pdfContent, pdfFileName));
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("integrationtestuser");
        user.setEmail("integration.test@example.com");
        user.setRole(Role.USER);
        return user;
    }

    private ShoppingList createTestShoppingList(User user) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(1L);
        shoppingList.setName("Integration Test Shopping List");
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

    private String formatShoppingListText(ShoppingList shoppingList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Shopping List: ").append(shoppingList.getName()).append("\n\n");

        if (shoppingList.getGeneratedFromRecipe() != null && !shoppingList.getGeneratedFromRecipe().isEmpty()) {
            sb.append("Generated from: ").append(shoppingList.getGeneratedFromRecipe()).append("\n\n");
        }

        sb.append("Items:\n");
        if (shoppingList.getListItems() != null) {
            for (int i = 0; i < shoppingList.getListItems().size(); i++) {
                ListItem item = shoppingList.getListItems().get(i);
                sb.append(String.format("%d. %s - %.1f %s\n",
                        i + 1, item.getName(), item.getAmount(), item.getUnit()));
            }
        }

        return sb.toString();
    }
}
