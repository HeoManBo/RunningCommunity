package com.example.runningweb.mongo;


import com.example.runningweb.domain.BaseEntity;
import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "message")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Message extends BaseEntity {

    @Id
    private String id;

    @Field("message")
    private String message; //전송한 메세지

    @Field("writer")
    private String writer; //작성자 닉네임

    @Field("roomNumber")
    private String roomNumber; //방 번호

    @Builder
    public Message(String message, Member writer, String chattingRoom) {
        this.message = message;
        this.writer = writer.getNickname();
        this.roomNumber = chattingRoom;
    }
}
