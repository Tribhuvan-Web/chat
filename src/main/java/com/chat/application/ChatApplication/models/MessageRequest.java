package com.chat.application.ChatApplication.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageRequest {

    private String content;
    private String sender;
    private LocalDateTime messageTime;
}
