package com.l1Akr.config;

import com.l1Akr.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

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
        log.info("开始添加拦截器...");
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
