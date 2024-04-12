package com.example.runningweb.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    private String message; //전송한 메세지


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id")
    private ChattingRoom chattingRoom; //메세지 전송한 채팅방


    @Builder
    public Message(String message, Member writer, ChattingRoom chattingRoom) {
        this.message = message;
        this.writer = writer;
        this.chattingRoom = chattingRoom;
    }
}
