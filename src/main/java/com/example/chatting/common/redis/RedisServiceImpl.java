package com.example.chatting.common.redis;

import com.example.chatting.common.websocket.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl {
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 메시지 발행
     * @param channel 채널
     * @param message 메시지
     */
    public void publish(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }
    /**
     * 메시지 구독
     * @param channel 채널
     * @param session WebSocketSession
     */
    public void subscribe(String channel, WebSocketSession session) {
        Objects.requireNonNull(stringRedisTemplate.getConnectionFactory())
                .getConnection()
                .subscribe(getMessageHandler(session), channel.getBytes());
    }
    /**
     * 여러 채널 메시지 구독
     * @param channel 채널
     * @param session WebSocketSession
     */
    public void subscribe(String[] channel, WebSocketSession session) {
        for (String c : channel){
            Objects.requireNonNull(stringRedisTemplate.getConnectionFactory())
                    .getConnection()
                    .subscribe(getMessageHandler(session), c.getBytes());
        }
    }
    /**
     * 메세지 핸들러 생성
     * @param session WebSocketSession
     */
    private RedisMessageHandler getMessageHandler(WebSocketSession session) {
        return new RedisMessageHandler(session);
    }
}

@Log4j2
@RequiredArgsConstructor
class RedisMessageHandler implements MessageListener {
    private final WebSocketSession session;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Redis 메시지 수신
     * @param message 메시지
     * @param pattern 패턴
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            WebSocketMessage webSocketMessage = objectMapper.readValue(message.getBody(), WebSocketMessage.class);
            if(session.isOpen() && !webSocketMessage.getPayload().getUsername().equals(session.getAttributes().get("username"))){
                session.sendMessage(new TextMessage(new String(message.getBody())));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
