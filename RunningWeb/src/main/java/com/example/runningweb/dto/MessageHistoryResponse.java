package com.example.runningweb.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MessageHistoryResponse {

    private String sender;
    private String message;
    private String sendAt;

    public MessageHistoryResponse(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public MessageHistoryResponse(String sender, String message, String sendAt) {
        this.sender = sender;
        this.message = message;
        this.sendAt = sendAt;
    }
}
