package femcoders25.mykitchen_hub.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

        @Value("${server.port:8080}")
        private String serverPort;

        @Bean
        public OpenAPI myKitchenHubOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                Server devServer = new Server();
                devServer.setUrl("http://localhost:" + serverPort);
                devServer.setDescription("Server URL in Development environment");

                Contact contact = new Contact();
                contact.setEmail("contact@mykitchenhub.com");
                contact.setName("MyKitchen Hub Team");
                contact.setUrl("https://www.mykitchenhub.com");

                License mitLicense = new License()
                                .name("MIT License")
                                .url("https://choosealicense.com/licenses/mit/");

                Info info = new Info()
                                .title("MyKitchen Hub API")
                                .version("1.0")
                                .contact(contact)
                                .description("This API exposes endpoints to manage MyKitchen Hub application. " +
                                                "It provides functionality for user management, recipe management, " +
                                                "ingredient tracking, and shopping list generation.")
                                .termsOfService("https://www.mykitchenhub.com/terms")
                                .license(mitLicense);

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(devServer))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT"))

                                                .addSchemas("ErrorResponse", new Schema<>()
                                                                .type("object")
                                                                .addProperty("success", new Schema<>().type("boolean"))
                                                                .addProperty("message", new Schema<>().type("string"))
                                                                .addProperty("timestamp",
                                                                                new Schema<>().type("string")
                                                                                                .format("date-time")))

                                                .addResponses("BadRequest",
                                                                apiResponse(400, "Invalid input or malformed request"))
                                                .addResponses("Unauthorized",
                                                                apiResponse(401, "Authentication required"))
                                                .addResponses("Forbidden", apiResponse(403,
                                                                "You do not have permission to access this resource"))
                                                .addResponses("NotFound",
                                                                apiResponse(404, "Requested resource not found"))
                                                .addResponses("InternalServerError",
                                                                apiResponse(500, "Internal server error"))
                                                .addResponses("NoContent", new ApiResponse()
                                                                .description("Successfully processed request with no content")));
        }

        private ApiResponse apiResponse(int status, String message) {
                return new ApiResponse()
                                .description(message)
                                .content(jsonError(status, message));
        }

        private Content jsonError(int status, String message) {
                return new Content().addMediaType("application/json",
                                new MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                                .example(Map.of("success", false, "message", message, "timestamp",
                                                                "2024-01-01T00:00:00Z")));
        }
}
