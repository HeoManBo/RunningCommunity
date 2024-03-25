package com.example.runningweb.dto;

import com.example.runningweb.domain.AttachFile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardViewDto {

    private Long boardId;
    private String title;
    private String content;
    private List<AttachFile> attachFiles;
    private String writer;
    private String writeAt; //작성 시간

    @Builder
    public BoardViewDto(Long boardId, String title, String content, List<AttachFile> attachFiles, String writer, String writeAt) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.attachFiles = attachFiles;
        this.writer = writer;
        this.writeAt = writeAt;
    }
}
