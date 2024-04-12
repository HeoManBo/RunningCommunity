package com.example.runningweb.domain;


import com.example.runningweb.chatting.domain.RedisChattingRoom;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class EnteredRoom extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entered_room_Id")
    private Long enteredRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id")
    private ChattingRoom chattingRoom; //참가중인 채팅방 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //




    @Builder
    public EnteredRoom(ChattingRoom chattingRoom, Member member) {
        this.chattingRoom = chattingRoom;
        this.member = member;
    }

}
