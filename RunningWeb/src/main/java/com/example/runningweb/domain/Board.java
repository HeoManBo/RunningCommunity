package com.example.runningweb.domain;

import com.example.runningweb.dto.BoardViewDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String content;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ColumnDefault("0")
    private int up;

    @ColumnDefault("0")
    private int down;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "board")
    private List<AttachFile> files = new ArrayList<>();

    private String writer; //작성자
    private int view; //조회수

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Board(String content, String title, Member member, String writer) {
        this.content = content;
        this.title = title;
        this.member = member;
        this.up = 0;
        this.down = 0;
        this.writer = writer;
        this.view = 0;
    }

    //== 연관관계 메소드 ==//

    // 파일 연관관계 등록
    public void saveFiles(AttachFile attachFile){
        files.add(attachFile);
        attachFile.assignBoard(this);
    }




    public void UpdateWriter(Member member) {
        //해당 게시글 작성자 설정 -> 이때 member는 게시판 업데이트를 하지 않음 -> 로그인시 게시판 정보를 가져오지 않았기 때문임.
        this.member = member;
    }


    public void updateBoard(BoardViewDto boardViewDto) {
        this.title = boardViewDto.getTitle();
        this.content = boardViewDto.getContent();
    }

    public void addView(){
        this.view += 1;
    }
}
