package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.user.entity.User;

public class ShoppingListEmailTemplates {
        public static String getShoppingListEmailPlainText(User user, String shoppingList) {
                return String.format(
                                "Hello %s! 🛒\n\n" +
                                                "Here is your freshly generated shopping list from MyKitchen Hub:\n\n" +
                                                "%s\n\n" +
                                                "📎 A PDF version of this shopping list is attached to this email for easy printing and offline use.\n\n"
                                                +
                                                "Happy cooking! 🍳\n\n" +
                                                "Best regards,\n" +
                                                "MyKitchen Hub Team 🚀",
                                user.getUsername(),
                                shoppingList);
        }

        public static String getShoppingListEmailHtml(User user, String shoppingList) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "  <meta charset=\"UTF-8\">" +
                                                "  <style>" +
                                                "    body { font-family: Arial, sans-serif; line-height: 1.6; background-color: #FDE6DB; color: #333; margin: 0; padding: 0; }"
                                                +
                                                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; box-sizing: border-box; }"
                                                +
                                                "    .header { background-color: #EF4B33; color: white; padding: 25px 20px; border-radius: 10px; text-align: center; margin-bottom: 20px; }"
                                                +
                                                "    .logo { max-width: 80px; height: auto; display: block; margin: 0 auto 15px auto; }"
                                                +
                                                "    .header h1 { margin: 0; font-size: 24px; font-weight: bold; }" +
                                                "    .content { background: white; padding: 25px; border-radius: 10px; margin: 20px 0; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }"
                                                +
                                                "    .shopping-list { background: #FDE6DB; padding: 20px; border-radius: 8px; border: 1px solid #E6D3C7; margin: 20px 0; }"
                                                +
                                                "    .shopping-list pre { margin: 0; white-space: pre-wrap; word-wrap: break-word; font-family: 'Courier New', monospace; font-size: 13px; line-height: 1.4; color: #333; }"
                                                +
                                                "    .pdf-info { background: #f0f8ff; padding: 20px; border-radius: 8px; margin-top: 25px; border-left: 4px solid #9CC4FE; }"
                                                +
                                                "    .pdf-info p { margin: 0; }" +
                                                "    .pdf-info .title { color: #333; font-weight: bold; font-size: 16px; margin-bottom: 8px; }"
                                                +
                                                "    .pdf-info .description { color: #666; font-size: 14px; line-height: 1.5; }"
                                                +
                                                "    .footer { text-align: center; color: #666; font-size: 14px; margin-top: 25px; padding-top: 20px; border-top: 1px solid #E6D3C7; }"
                                                +
                                                "    .greeting { font-size: 18px; margin-bottom: 15px; }" +
                                                "    .intro { margin-bottom: 20px; color: #555; }" +
                                                "    .cooking-note { text-align: center; font-size: 16px; color: #EF4B33; font-weight: bold; margin: 20px 0; }"
                                                +
                                                "  </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "  <div class=\"container\">" +
                                                "    <div class=\"header\">" +
                                                "      <img src=\"cid:logo\" alt=\"MyKitchen Hub\" class=\"logo\" />" +
                                                "      <h1>🛒 Your Shopping List</h1>" +
                                                "    </div>" +
                                                "    <div class=\"content\">" +
                                                "      <div class=\"greeting\">Hello <strong>%s</strong> 👋</div>" +
                                                "      <div class=\"intro\">Here is your freshly generated shopping list:</div>"
                                                +
                                                "      <div class=\"shopping-list\">" +
                                                "        <pre>%s</pre>" +
                                                "      </div>" +
                                                "      <div class=\"cooking-note\">Happy cooking! 🍳</div>" +
                                                "      <div class=\"pdf-info\">" +
                                                "        <p class=\"title\">📎 PDF Attachment</p>" +
                                                "        <p class=\"description\">A PDF version of this shopping list is attached for easy printing and offline use.</p>"
                                                +
                                                "      </div>" +
                                                "    </div>" +
                                                "    <div class=\"footer\">" +
                                                "      <p>Best regards,<br>MyKitchen Hub Team 🚀</p>" +
                                                "    </div>" +
                                                "  </div>" +
                                                "</body>" +
                                                "</html>",
                                user.getUsername(),
                                shoppingList);
        }
}
