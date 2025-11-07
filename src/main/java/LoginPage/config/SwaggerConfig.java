package LoginPage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Login Page API")
                .version("1.0")
                .description("API documentation for React + Spring Boot login and register system")
                .contact(new Contact()
                    .name("Your Name")
                    .email("youremail@example.com")
                    .url("https://yourwebsite.com"))
            );
    }
}

