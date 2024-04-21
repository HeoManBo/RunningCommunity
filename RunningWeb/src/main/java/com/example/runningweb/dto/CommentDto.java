package com.example.runningweb.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 보여줄 댓글 내용
 */
@Data
@NoArgsConstructor
public class CommentDto {

    private String writer; //작성자
    private String content; //댓글 내용
    private LocalDateTime wroteAt; //작성일자 년,월,일 시간,분
    private Long comment_id; //댓글 ID
    private Long writer_id; // 작성자_ID
    private List<CommentDto> child = new ArrayList<>();

    @Builder
    public CommentDto(String writer, String content, LocalDateTime wroteAt, Long comment_id, Long writer_id) {
        this.writer = writer;
        this.content = content;
        this.wroteAt = wroteAt;
        this.comment_id = comment_id;
        this.writer_id = writer_id;
    }

    public void addChild(CommentDto commentDto) {
        child.add(commentDto);
    }

}
