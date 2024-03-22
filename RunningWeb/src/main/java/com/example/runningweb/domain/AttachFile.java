package com.example.runningweb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serverFileName; //서버에 저장한 파일 이름
    private String originalName; //유저가 전달한 파일 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    @Builder
    public AttachFile(String serverFileName, String originalName, Board board) {
        this.serverFileName = serverFileName;
        this.originalName = originalName;
        this.board = board;
    }

    public void assignBoard(Board board){
        this.board = board;
    }

    //확장자 추출
    public String extractExt(){
        int pos = originalName.lastIndexOf('.');
        if(pos == -1) return "no";
        return originalName.substring(pos+1);
    }
}
