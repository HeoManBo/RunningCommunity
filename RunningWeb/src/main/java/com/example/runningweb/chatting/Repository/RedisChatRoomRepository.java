package com.example.runningweb.chatting.Repository;

import com.example.runningweb.chatting.domain.RedisChattingRoom;
import com.example.runningweb.domain.Member;
import com.example.runningweb.service.ChattingRoomService;
import com.example.runningweb.service.EnteredRoomService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Repository
@RequiredArgsConstructor
public class RedisChatRoomRepository {

    //redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private static final String USER_COUNT = "USER_COUNT";
    private static final String ENTER_INFO = "ENTER_INFO";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, RedisChattingRoom> opsHashChatRoom; // (CHAT_ROOMS), (roomId(UUID), chatRoom) 형태로 저장
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo; // (chat_Room), (roomId, USERINFO)
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    private final ChattingRoomService roomService;
    private final EnteredRoomService enteredRoomService;

    //모든 채팅방 조회
    public List<RedisChattingRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public RedisChattingRoom findById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    // 채팅방 생성 : 서버간 공유를 위해 Redis에 저장한다.
    @Transactional
    public RedisChattingRoom createChattingRoom(String name, String region, Member member) {
        RedisChattingRoom redisChattingRoom = new RedisChattingRoom(name, region);

        opsHashChatRoom.put(CHAT_ROOMS, redisChattingRoom.getRoomId(), redisChattingRoom);
        Long success = roomService.createChattingRoom(name, redisChattingRoom.getRoomId(), region, member);

        if(success == null){
            throw new IllegalArgumentException();
        }

        return redisChattingRoom;
    }

    // 유저가 입장한 채팅방 ID와 유저의 세션 ID 매핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 조회
    public String getUserEnterRoomId(String sessionId){
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션 정보와 매핑된 채팅방 ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    //채팅방 유저수 조회
    public long getUserCount(String roomId){
        return Long.parseLong(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    //채팅방에 입장한 유저수 + 1
    public long addUserCount(String roomId){
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    //퇴장시 - 1
    public long minusUserCount(String roomId){
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).orElse(0L);
    }


}
