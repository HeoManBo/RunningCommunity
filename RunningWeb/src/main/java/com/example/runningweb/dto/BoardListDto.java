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
    private int commentCnt; // 댓글 수
    private int startPage; //현재 목록의 맨 앞 페이지 번호
    private int endPage; //현재 목록의 마지막 게시판 페이지 번호
    private int lastPageNum; //마지막 조회가능한 페이지 번호

    @Builder
    public BoardListDto(Long boardId, String title, String writer, String wroteAt,
                        int commentCnt, int startPage, int endPage, int lastPageNum) {
        this.boardId = boardId;
        this.title = title;
        this.writer = writer;
        this.wroteAt = wroteAt;
        this.commentCnt = commentCnt;
        this.startPage = startPage;
        this.endPage = endPage;
        this.lastPageNum = lastPageNum;
    }
}
