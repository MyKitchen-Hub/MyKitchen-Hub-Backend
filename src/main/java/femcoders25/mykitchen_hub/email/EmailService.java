package femcoders25.mykitchen_hub.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendShoppingListCreatedEmail(String to, String subject, String plainText, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("mykitchenhub.project@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(plainText, htmlContent);
        ClassPathResource logo = new ClassPathResource("static/images/logo.png");
        helper.addInline("logo", logo);
        mailSender.send(message);
    }

    public void sendShoppingListCreatedEmailWithPdf(String to, String subject, String plainText, String htmlContent, 
                                                   byte[] pdfAttachment, String pdfFileName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("mykitchenhub.project@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(plainText, htmlContent);

        ClassPathResource logo = new ClassPathResource("static/images/logo.png");
        helper.addInline("logo", logo);

        if (pdfAttachment != null && pdfAttachment.length > 0) {
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfAttachment));
        }
        
        mailSender.send(message);
    }

    public void sendUserWelcomeEmail(String to, String subject, String plainText, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("mykitchenhub.project@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(plainText, htmlContent);
        ClassPathResource logo = new ClassPathResource("static/images/logo.png");
        helper.addInline("logo", logo);
        mailSender.send(message);
    }
}
