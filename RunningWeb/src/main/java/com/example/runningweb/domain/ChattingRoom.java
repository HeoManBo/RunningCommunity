package com.example.runningweb.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChattingRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_id")
    private Long chattingRoomId;

    private String title;
    private String region;
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Builder
    public ChattingRoom(String title, String region, String uuid, Member member) {
        this.title = title;
        this.region = region;
        this.uuid = uuid;
        this.member = member;
    }


}
