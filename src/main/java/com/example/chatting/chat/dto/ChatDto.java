package com.example.chatting.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatDto {
    private String message;
    private final Long chatRoomId;
    private final String username;

    @JsonCreator
    public ChatDto(@JsonProperty("chatRoomId") Long chatRoomId,
                   @JsonProperty("username") String username,
                   @JsonProperty("message") String message) {
        this.chatRoomId = chatRoomId;
        this.username = username;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
