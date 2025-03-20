package com.l1Akr.config;

import com.l1Akr.interceptor.LogInterceptor;
import com.l1Akr.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Configuration
@Slf4j
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    public void addResourceHandlers(ResourceHandlerRegistry registry){
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login"
                        , "/logout"
                        , "/user/register"
                        , "/**/*.html"
                        , "/**/*.js"
                        , "/**/*.css"
                        , "/swagger-ui/**"
                        , "/v3/api-docs/**"
                        , "/error"
                        , "/doc.html");
    }

}
