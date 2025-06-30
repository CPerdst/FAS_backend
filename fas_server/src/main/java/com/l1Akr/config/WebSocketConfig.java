package com.l1Akr.config;


import com.l1Akr.interceptor.WebSocketAuthInterceptor;
import com.l1Akr.websocket.handler.SampleStatusWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sampleStatusWebSocketHandler(), "/ws/sampleStatus")
                .setAllowedOrigins("*")
                .addInterceptors(webSocketAuthInterceptor());
    }

    @Bean
    public SampleStatusWebSocketHandler sampleStatusWebSocketHandler() {
        return new SampleStatusWebSocketHandler();
    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor();
    }

}
