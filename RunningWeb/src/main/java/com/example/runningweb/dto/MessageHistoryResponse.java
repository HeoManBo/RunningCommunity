package com.example.runningweb.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageHistoryResponse {

    private String sender;
    private String message;

    public MessageHistoryResponse(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
