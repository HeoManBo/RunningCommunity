package com.example.runningweb.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardUpdateRequest {

    private BoardViewDto boardViewDto;
    private List<MultipartFile> newAttachFiles; //새로운 파일

    @Builder
    public BoardUpdateRequest(BoardViewDto boardViewDto, List<MultipartFile> newAttachFile) {
        this.boardViewDto = boardViewDto;
        this.newAttachFiles = newAttachFile;
    }

}
