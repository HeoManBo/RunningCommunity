package com.example.runningweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentEvent {

    private String boardId;
    private String userId;

    public CommentEvent(String boardId, String userId) {
        this.boardId = boardId;
        this.userId = userId;
    }

}
