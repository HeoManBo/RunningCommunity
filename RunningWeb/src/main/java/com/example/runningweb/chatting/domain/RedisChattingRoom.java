package com.example.runningweb.chatting.domain;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

//Redis에 저장되려면 직렬화가 가능해야함
@Getter
@Setter
public class RedisChattingRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private final String roomId; // UUID
    private String name; //방 제목
    private long userCount; //유저 수

    public RedisChattingRoom(String name) {
        this.name = name;
        this.roomId = UUID.randomUUID().toString();
    }

    public void updateRoomName(String name){
        this.name = name;
    }

}
