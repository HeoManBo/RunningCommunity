package com.example.runningweb.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardDto {

    private String title;
    private String content;
    private List<MultipartFile> attachFile;


    @Builder
    public BoardDto(String title, String content, List<MultipartFile> attachFile) {
        this.title = title;
        this.content = content;
        this.attachFile = attachFile;
    }
}
