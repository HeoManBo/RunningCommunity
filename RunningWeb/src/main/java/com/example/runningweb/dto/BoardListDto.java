package com.example.runningweb.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//메인화면에서 board list를 보여주기 위함
@Data
@NoArgsConstructor
public class BoardListDto {

    private Long boardId;
    private String title;
    private String writer;
    private String wroteAt;

    @Builder
    public BoardListDto(Long boardId, String title, String writer, String wroteAt) {
        this.boardId = boardId;
        this.title = title;
        this.writer = writer;
        this.wroteAt = wroteAt;
    }
}
