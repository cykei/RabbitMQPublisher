package com.example.rabbitmqpublisher.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    // 채팅 방 관리를 위한 웹소켓 세션 id - 세션 맵
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 웹소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session); // 새로운 세션(사용자)을 저장

        Message message = Message.builder()
                .sender(sessionId)
                .receiver("all")
                .build();
        message.newConnect();

        sessions.values().forEach(s -> { // 모든 세션(다른 사용자)에게 새로운 새션의 등장을 알림
            try {
                if (!s.getId().equals(sessionId)) {
                    s.sendMessage(new TextMessage(Utils.getString(message)));
                }
            } catch (Exception e) {

            }
        });
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    // 양방향 데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Message message = Utils.getObject(textMessage.getPayload());
        message.setSender(session.getId());

        WebSocketSession receiver = sessions.get(message.getReceiver());
        if (receiver != null && receiver.isOpen()) {
            // 타겟이 존재하고 연결된 상태라면 메시지를 전송한다.
            receiver.sendMessage(new TextMessage(Utils.getString(message)));
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    // 소켓 통신 에러
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    // 소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);

        final Message message = new Message();
        message.closeConnect();
        message.setSender(sessionId);

        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(Utils.getString(message)));
            } catch (Exception e) {

            }
        });
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
}
