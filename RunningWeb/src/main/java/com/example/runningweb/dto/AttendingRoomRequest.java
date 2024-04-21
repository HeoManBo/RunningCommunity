package com.example.runningweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendingRoomRequest {

    private String roomId; // UUID
    private String name; //방 제목
    private long userCount; //유저 수
    private String region;


    public AttendingRoomRequest(String roomId, String name, String region) {
        this.roomId = roomId;
        this.name = name;
        this.region = region;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }
}
