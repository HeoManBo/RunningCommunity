package com.example.runningweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendingRoomRequest {

    private String roomId; // UUID
    private String name; //방 제목
    private long userCount; //유저 수


    public AttendingRoomRequest(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }
}
