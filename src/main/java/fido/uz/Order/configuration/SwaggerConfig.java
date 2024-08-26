package fido.uz.Order.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.SpringDocAnnotationsUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.ui.open}")
    private Boolean open;
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                )
                .info(new Info().title("Order Service").description(
                        "This is a sample Spring Boot REST full service using springdoc-openapi and OpenAPI 3.").version("v1.0.0"))
                .openapi("3.0.2")

                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
    @Bean
    SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }

    @Bean
    public CommandLineRunner openSwaggerUI(SwaggerUIOpener swaggerUIOpener) {
        return args -> {
            if (open) {
                swaggerUIOpener.openSwaggerUI();
            }
        };
    }
    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> {
            Schema<?> schema = SpringDocAnnotationsUtils.extractSchema(
                    openApi.getComponents(),
                    ResultAsync.class,
                    null,
                    null
            );
            MediaType mediaType = new MediaType().schema(schema);
            Content content = new Content().addMediaType(APPLICATION_JSON_VALUE, mediaType);
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                if (operation.getParameters() != null) {
                    operation.getParameters().add(createAcceptLanguageParam());
                } else {
                    List<Parameter> parameters = new ArrayList<>();
                    parameters.add(createAcceptLanguageParam());
                    operation.setParameters(parameters);
                }
                ApiResponses apiResponses = operation.getResponses();
                apiResponses.addApiResponse("400", new ApiResponse()
                        .description("Some of the required fields are not filled or invalid request body case.").content(content));
                apiResponses.addApiResponse("401", new ApiResponse()
                        .description("Authorization error case.").content(content));
                apiResponses.addApiResponse("403", new ApiResponse()
                        .description("Forbidden.").content(content));
                apiResponses.addApiResponse("404", new ApiResponse()
                        .description("Resource Not Found.").content(content));
                apiResponses.addApiResponse("500", new ApiResponse()
                        .description("Internal Error").content(content));
            }));
        };
    }
    private static Parameter createAcceptLanguageParam() {

        List<String> acceptLanguages = new ArrayList<>(Arrays.asList(
                AcceptLanguage.RU.toString(),
                AcceptLanguage.UZL.toString(),
                AcceptLanguage.UZC.toString(),
                AcceptLanguage.EN.toString()
        ));
        Parameter acceptLanguageParam = new Parameter();
        acceptLanguageParam.setName("Accept-Language");
        acceptLanguageParam.setIn("header");
        acceptLanguageParam.setRequired(true);
        Schema<String> defaultRuSchema = new Schema<>();
        defaultRuSchema.setDefault(AcceptLanguage.EN);
        defaultRuSchema.setType("string");
        defaultRuSchema.setNullable(false);
        defaultRuSchema.setEnum(acceptLanguages);
        acceptLanguageParam.setSchema(defaultRuSchema);
        return acceptLanguageParam;
    }
}