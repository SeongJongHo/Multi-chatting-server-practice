package com.example.chatting.chatRoom;

import com.example.chatting.chat.dto.ChatDto;
import com.example.chatting.common.redis.RedisServiceImpl;
import com.example.chatting.common.websocket.WebSocketMessage;
import com.example.chatting.common.websocket.WebSocketMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Log4j2
@Getter
@Component
@RequiredArgsConstructor
public class ChatRoom {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisServiceImpl redisService;

    /**
     * 채팅방 입장
     * @param chatDto ChatDto
     * @param session 웹소켓 세션
     */
    public void enter(ChatDto chatDto, WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        String channel = "chatRoom:"+chatDto.getChatRoomId();
        redisService.subscribe(channel, session);

        chatDto.setMessage(username + "님이 입장하셨습니다.");
        redisService.publish(channel, getTextMessage(WebSocketMessageType.ENTER, chatDto));
    }

    /**
     * 메시지 전송
     * @param chatDto ChatDto
     */
    public void sendMessage(ChatDto chatDto) {
        String channel = "chatRoom:"+chatDto.getChatRoomId();
        redisService.publish(channel, getTextMessage(WebSocketMessageType.TALK, chatDto));
    }

    /**
     * 메시지 전송
     * @param type 메시지 타입
     * @param chatDto ChatDto
     * @return String
     */
    private String getTextMessage(WebSocketMessageType type, ChatDto chatDto) {
        try {
            return objectMapper.writeValueAsString(new WebSocketMessage(type, chatDto));
        }catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
