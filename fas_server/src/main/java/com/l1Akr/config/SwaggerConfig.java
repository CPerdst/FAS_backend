package com.l1Akr.config;

//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//@EnableKnife4j
//public class SwaggerConfig {
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.l1Akr.controllers")) // 替换你的Controller包路径
//                .paths(PathSelectors.any())
//                .build();
//    }
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("FAS项目API文档")
//                .description("FAS (File Analysis System) 文件分析系统")
//                .version("1.0")
//                .build();
//    }
//}
