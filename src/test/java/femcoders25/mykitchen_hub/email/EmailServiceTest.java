package femcoders25.mykitchen_hub.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_SUBJECT = "Test Subject";
    private final String TEST_PLAIN_TEXT = "Plain text content";
    private final String TEST_HTML_CONTENT = "<html><body>HTML content</body></html>";

    @BeforeEach
    void setUp() {
    }

    @Test
    void sendUserWelcomeEmail_ShouldSendEmailSuccessfully() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendUserWelcomeEmail(TEST_EMAIL, TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendShoppingListCreatedEmail_ShouldSendEmailSuccessfully() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendShoppingListCreatedEmail(TEST_EMAIL, TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldSendEmailWithPdfAttachment() throws MessagingException {
        byte[] pdfContent = "PDF content".getBytes();
        String pdfFileName = "shopping_list.pdf";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendShoppingListCreatedEmailWithPdf(
                TEST_EMAIL, TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT, pdfContent, pdfFileName);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldSendEmailWithoutPdf_WhenPdfIsNull() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendShoppingListCreatedEmailWithPdf(
                TEST_EMAIL, TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT, null, "test.pdf");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldSendEmailWithoutPdf_WhenPdfIsEmpty() throws MessagingException {
        byte[] emptyPdf = new byte[0];
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendShoppingListCreatedEmailWithPdf(
                TEST_EMAIL, TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT, emptyPdf, "test.pdf");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendUserWelcomeEmail_ShouldThrowException_WhenEmailIsNull() throws MessagingException {
        assertThrows(NullPointerException.class,
                () -> emailService.sendUserWelcomeEmail(null, TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT));
    }

    @Test
    void sendShoppingListCreatedEmail_ShouldThrowException_WhenEmailIsNull() throws MessagingException {
        assertThrows(NullPointerException.class, () -> emailService.sendShoppingListCreatedEmail(null, TEST_SUBJECT,
                TEST_PLAIN_TEXT, TEST_HTML_CONTENT));
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldThrowException_WhenEmailIsNull() throws MessagingException {
        assertThrows(NullPointerException.class, () -> emailService.sendShoppingListCreatedEmailWithPdf(null,
                TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT, new byte[0], "test.pdf"));
    }

    @Test
    void sendUserWelcomeEmail_ShouldThrowException_WhenEmailIsEmpty() throws MessagingException {
        assertThrows(NullPointerException.class,
                () -> emailService.sendUserWelcomeEmail("", TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT));
    }

    @Test
    void sendShoppingListCreatedEmail_ShouldThrowException_WhenEmailIsEmpty() throws MessagingException {
        assertThrows(NullPointerException.class,
                () -> emailService.sendShoppingListCreatedEmail("", TEST_SUBJECT, TEST_PLAIN_TEXT, TEST_HTML_CONTENT));
    }

    @Test
    void sendShoppingListCreatedEmailWithPdf_ShouldThrowException_WhenEmailIsEmpty() throws MessagingException {
        byte[] pdfContent = "PDF content".getBytes();

        assertThrows(NullPointerException.class,
                () -> emailService.sendShoppingListCreatedEmailWithPdf("", TEST_SUBJECT, TEST_PLAIN_TEXT,
                        TEST_HTML_CONTENT, pdfContent, "test.pdf"));
    }
}
