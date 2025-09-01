package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.shoppinglist.entity.ListItem;
import femcoders25.mykitchen_hub.shoppinglist.entity.ShoppingList;
import femcoders25.mykitchen_hub.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 20;
    private static final float TITLE_FONT_SIZE = 18;
    private static final float HEADER_FONT_SIZE = 14;
    private static final float BODY_FONT_SIZE = 12;

    public byte[] generateShoppingListPdf(ShoppingList shoppingList, User user) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setNonStrokingColor(253, 230, 219);
            contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
            contentStream.fill();

            contentStream.setNonStrokingColor(0, 0, 0);

            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            yPosition = drawHeader(contentStream, page, yPosition);
            yPosition = drawTitle(contentStream, page, yPosition, shoppingList.getName());
            yPosition = drawUserInfo(contentStream, page, yPosition, user);
            yPosition = drawShoppingListDetails(contentStream, page, yPosition, shoppingList);
            yPosition = drawItemsList(contentStream, page, yPosition, shoppingList.getListItems());
            drawFooter(contentStream, page, yPosition);
            
            contentStream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("Failed to generate PDF for shopping list: {}", shoppingList.getId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private float drawHeader(PDPageContentStream contentStream, PDPage page, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE);
        contentStream.setNonStrokingColor(239, 75, 51);
        String title = "MyKitchen Hub";
        float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(title) / 1000 * TITLE_FONT_SIZE;
        float titleX = (page.getMediaBox().getWidth() - titleWidth) / 2;
        contentStream.beginText();
        contentStream.newLineAtOffset(titleX, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        
        return yPosition - LINE_HEIGHT - 10;
    }

    private float drawTitle(PDPageContentStream contentStream, PDPage page, float yPosition, String listName) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        contentStream.setNonStrokingColor(0, 0, 0);
        
        String title = "Shopping List: " + listName;
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        
        return yPosition - LINE_HEIGHT - 15;
    }

    private float drawUserInfo(PDPageContentStream contentStream, PDPage page, float yPosition, User user) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, BODY_FONT_SIZE);
        
        String userInfo = "Generated for: " + user.getUsername() + " (" + user.getEmail() + ")";
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(userInfo);
        contentStream.endText();
        
        return yPosition - LINE_HEIGHT - 10;
    }

    private float drawShoppingListDetails(PDPageContentStream contentStream, PDPage page, float yPosition, ShoppingList shoppingList) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, BODY_FONT_SIZE);

        if (shoppingList.getGeneratedFromRecipe() != null && !shoppingList.getGeneratedFromRecipe().isEmpty()) {
            String recipesInfo = "Generated from: " + shoppingList.getGeneratedFromRecipe();
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(recipesInfo);
            contentStream.endText();
            yPosition -= LINE_HEIGHT;
        }

        String createdInfo = "Created: " + shoppingList.getCreatedAt().format(DATE_FORMATTER);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(createdInfo);
        contentStream.endText();
        
        return yPosition - LINE_HEIGHT - 15;
    }

    private float drawItemsList(PDPageContentStream contentStream, PDPage page, float yPosition, List<ListItem> items) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);

        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Shopping Items:");
        contentStream.endText();
        yPosition -= LINE_HEIGHT + 5;

        contentStream.setFont(PDType1Font.HELVETICA, BODY_FONT_SIZE);
        
        for (int i = 0; i < items.size(); i++) {
            ListItem item = items.get(i);
            String itemText = String.format("%d. %s - %.1f %s", 
                i + 1, item.getName(), item.getAmount(), item.getUnit());
            
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN + 20, yPosition);
            contentStream.showText(itemText);
            contentStream.endText();
            
            yPosition -= LINE_HEIGHT;

            if (yPosition < MARGIN + 50) {
                contentStream.close();
                return yPosition;
            }
        }
        
        return yPosition;
    }

    private void drawFooter(PDPageContentStream contentStream, PDPage page, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(102, 102, 102);
        
        String footer = "Generated by MyKitchen Hub - Happy cooking!";
        float footerWidth = PDType1Font.HELVETICA.getStringWidth(footer) / 1000 * 10;
        float footerX = (page.getMediaBox().getWidth() - footerWidth) / 2;
        
        contentStream.beginText();
        contentStream.newLineAtOffset(footerX, yPosition);
        contentStream.showText(footer);
        contentStream.endText();
    }
}
