package com.l1Akr.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API文档")
                        .description("接口说明")
                        .version("1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Knife4j文档")
                        .url("https://doc.xiaominfo.com"));
    }
}