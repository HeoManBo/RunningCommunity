package com.example.runningweb.dto;

public class CommentEvent {

    private String boardId;

    public CommentEvent(String boardId) {
        this.boardId = boardId;
    }

    public String getBoardId(){
        return boardId;
    }

}
