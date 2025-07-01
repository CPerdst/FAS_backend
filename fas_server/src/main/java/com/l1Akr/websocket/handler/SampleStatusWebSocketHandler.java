package com.l1Akr.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.l1Akr.common.result.WsMessage;
import com.l1Akr.common.serializer.LocalDateTimeSerializer;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import com.l1Akr.pojo.vo.SampleStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SampleStatusWebSocketHandler implements WebSocketHandler {

    private static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getAttributes().get("userId").toString();
        sessions.put(userId, session);
        log.info("用户 {} 建立了WebSocket链接", userId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            String payload = ((TextMessage)message).getPayload();
            if (payload.equals("ping")) {
                log.info("用户 {} 心跳", session.getAttributes().get("userId").toString());
                session.sendMessage(new TextMessage("pong"));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: {}", exception.getMessage());
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userId = session.getAttributes().get("userId").toString();
        sessions.remove(userId);
        log.info("用户 {} 断开WebSocket链接", userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void notifyUserWithNewStatus(Integer userId, Integer sampleId, Integer status) {
        WebSocketSession session = sessions.get(userId.toString());
        if (session == null) return;
        if (session.isOpen()) {
            SampleStatusVO sampleStatusVO = new SampleStatusVO(sampleId, status);
            String toJson = new WsMessage<>(0, "SampleStatusUpdate", sampleStatusVO).parse();
            WebSocketMessage<String> message = new TextMessage(toJson);
            try {
                session.sendMessage(message);
            } catch (Exception e) {
                log.error("用户 {} 发送消息失败: {}", userId, e.getMessage());
            }
        } else {
            log.warn("用户 {} WebSocket链接出现错误", userId);
            sessions.remove(userId.toString());
        }
    }

    public void notifyUserWithSampleUploaded(Integer userId, SampleBaseLightDTO sampleBaseLightDTO) {
        WebSocketSession session = sessions.get(userId.toString());
        if (session == null) return;
        if (session.isOpen()) {
            String toJson = new WsMessage<>(0, "SampleUploadSuccess", sampleBaseLightDTO).parse();
            WebSocketMessage<String> message = new TextMessage(toJson);
            try {
                session.sendMessage(message);
            } catch (Exception e) {
                log.error("用户 {} 发送消息失败: {}", userId, e.getMessage());
            }
        } else {
            log.warn("用户 {} WebSocket链接出现错误", userId);
            sessions.remove(userId.toString());
        }
    }

}
