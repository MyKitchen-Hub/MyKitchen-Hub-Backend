package femcoders25.mykitchen_hub.email;

import femcoders25.mykitchen_hub.user.entity.User;

public class UserEmailTemplates {
        public static String getUserWelcomeEmailPlainText(User user) {
                return String.format(
                                "Hello %s! 👋\n\n" +
                                                "Welcome to MyKitchen Hub! 🎉\n\n" +
                                                "We're excited to have you join our community of food lovers.\n" +
                                                "Here you can explore recipes, share your own, and generate personalized shopping lists.\n\n"
                                                +
                                                "Best regards,\n" +
                                                "MyKitchen Hub Team 🚀",
                                user.getUsername());
        }

        public static String getUserWelcomeEmailHtml(User user) {
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
                                                "    .button { display: inline-block; background: #9CC4FE; color: black !important; padding: 12px 24px; text-decoration: none; border-radius: 8px; margin-top: 20px; font-weight: bold; font-size: 16px; }"
                                                +
                                                "    .footer { text-align: center; color: #666; font-size: 14px; margin-top: 25px; padding-top: 20px; border-top: 1px solid #E6D3C7; }"
                                                +
                                                "    .greeting { font-size: 18px; margin-bottom: 15px; }" +
                                                "    .intro { margin-bottom: 20px; color: #555; }" +
                                                "    .features { margin: 20px 0; }" +
                                                "    .features ul { margin: 0; padding-left: 20px; }" +
                                                "    .features li { margin-bottom: 8px; color: #555; }" +
                                                "    .button-container { text-align: center; margin: 25px 0; }" +
                                                "  </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "  <div class=\"container\">" +
                                                "    <div class=\"header\">" +
                                                "      <img src=\"cid:logo\" alt=\"MyKitchen Hub\" class=\"logo\" />" +
                                                "      <h1>🎉 Welcome to MyKitchen Hub!</h1>" +
                                                "    </div>" +
                                                "    <div class=\"content\">" +
                                                "      <div class=\"greeting\">Hello <strong>%s</strong> 👋</div>" +
                                                "      <div class=\"intro\">We're thrilled to have you join our community of food lovers!</div>"
                                                +
                                                "      <div class=\"features\">" +
                                                "        <ul>" +
                                                "          <li>Explore delicious recipes</li>" +
                                                "          <li>Create your own and share them</li>" +
                                                "          <li>Generate smart shopping lists</li>" +
                                                "        </ul>" +
                                                "      </div>" +
                                                "      <div class=\"button-container\">" +
                                                "        <a href=\"http://localhost:3000/login/\" class=\"button\">Log In Now</a>"
                                                +
                                                "      </div>" +
                                                "    </div>" +
                                                "    <div class=\"footer\">" +
                                                "      <p>Best regards,<br>MyKitchen Hub Team 🚀</p>" +
                                                "    </div>" +
                                                "  </div>" +
                                                "</body>" +
                                                "</html>",
                                user.getUsername());
        }
}
