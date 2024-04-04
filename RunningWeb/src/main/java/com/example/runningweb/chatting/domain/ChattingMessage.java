package com.example.runningweb.chatting.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChattingMessage {

    private MessageType type;
    private String roomId; //채팅 메세지를 보낼 방 번호
    private String message; //메세지
    private String sender; //보낸 사람 닉네임 --> 추후 로그인 한 사람의 Nickname으로 변경해야 함.

}
