package fido.uz.Order.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class SwaggerUIOpener {

    @Value("${server.port}")
    private String port;

    private static String swaggerUiUrl;

    @PostConstruct
    public void init() {
        swaggerUiUrl = "http://localhost:" + port + "/swagger-ui/index.html";
    }

    public void openSwaggerUI() {
        if (swaggerUiUrl == null || swaggerUiUrl.isEmpty()) {
            System.err.println("Swagger UI URL is not set. Please check the application.yml file.");
            return;
        }

        try {
            URI uri = new URI(swaggerUiUrl);

            // Detect the operating system
            String os = System.getProperty("os.name").toLowerCase();

            // Execute the command based on the operating system
            if (os.contains("win")) {
                // Windows
                String command = "rundll32 url.dll,FileProtocolHandler " + uri.toString();
                Runtime.getRuntime().exec(command);
            } else if (os.contains("mac")) {
                // macOS
                String command = "open " + uri.toString();
                Runtime.getRuntime().exec(command);
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux
                String command = "xdg-open " + uri.toString();
                Runtime.getRuntime().exec(command);
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + os);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
