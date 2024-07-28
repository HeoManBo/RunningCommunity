package com.example.runningweb.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CommentCreateRequest {

    private String content; //댓글 내용
    private Long parentId; //부모 댓글 번호


}
