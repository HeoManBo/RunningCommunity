package com.example.runningweb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

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
    private int like;

    @ColumnDefault("0")
    private int dislike;

    public Board(String content, String title, Member member) {
        this.content = content;
        this.title = title;
        this.member = member;
        this.like = 0;
        this.dislike = 0;
    }
}
