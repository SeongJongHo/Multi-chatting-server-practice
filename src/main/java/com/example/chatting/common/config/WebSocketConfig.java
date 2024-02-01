package com.example.chatting.common.config;

import com.example.chatting.chat.handler.WebSocketChatHandler;
import com.example.chatting.common.interceptor.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketChatHandler webSocketChatHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    /**
     * 웹소켓 연결을 위한 설정
     * 웹소켓 연결 EndPoint: ws://localhost:8080/chats
     * 에 연결시 동작할 핸들러는 webSocketChatHandler
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketChatHandler, "/chats")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOrigins("*");
    }
}