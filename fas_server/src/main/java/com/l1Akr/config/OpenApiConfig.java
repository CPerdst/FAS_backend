package com.l1Akr.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("FAS API 文档")
                                .version("v1.0")
                                .description("File Analysis System API"));
        }

        @Bean
        public GroupedOpenApi groupedOpenApi() {
                return GroupedOpenApi.builder()
                        .group("user-api")
                        .packagesToScan("com.l1Akr.controller")
                        .build();
        }
}