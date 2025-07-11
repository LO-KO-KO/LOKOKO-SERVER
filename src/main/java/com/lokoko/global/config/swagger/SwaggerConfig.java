package com.lokoko.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String JWT_SCHEME = "jwtAuth";

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addTagsItem(new Tag().name("AUTH"))
                .addTagsItem(new Tag().name("YOUTUBE"))
                .addTagsItem(new Tag().name("PRODUCT"))
                .addTagsItem(new Tag().name("PRODUCT LIKE"))
                .addTagsItem(new Tag().name("REVIEW"))
                .addTagsItem(new Tag().name("REVIEW LIKE"))
                .addTagsItem(new Tag().name("ADMIN"))
                /*
                 * TODO: 나머지 컨트롤러 완성시 추가 예정
                 */
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(JWT_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        )
                )
                .info(apiInfo());
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/**")
                .packagesToScan("com.lokoko")
                .packagesToExclude("com.lokoko.global.common.exception")
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("LOCOCO")
                .description("LOCOCO API 문서")
                .version("1.1.0");
    }
}
