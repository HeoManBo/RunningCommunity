package com.example.runningweb.chatting.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChattingMessage {

    private MessageType type;
    private String roomId; //채팅 메세지를 보낼 방 번호
    private String message; //메세지
    private String sender; //보낸 사람 닉네임 --> 추후 로그인 한 사람의 Nickname으로 변경해야 함.

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime sendAt; // 보낸 시간

    //입장, 퇴장시에먼 갱신해도 되지만, 수신하지 못하는 경우에도 (redis는 발행의 신뢰성을 보장하지 않음) 유저수가 갱신되도록 메시지 수신
    //할때마다 갱신되도록 설정함.
    private long userCount;

    public ChattingMessage(){}

    @Builder
    public ChattingMessage(MessageType type, String roomId, String message, String sender, int userCount, LocalDateTime sendAt) {
        this.type = type;
        this.roomId = roomId;
        this.message = message;
        this.sender = sender;
        this.userCount = userCount;
        this.sendAt = sendAt;
    }
}
