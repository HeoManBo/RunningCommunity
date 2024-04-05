package com.example.runningweb.chatting.Repository;

import com.example.runningweb.chatting.domain.ChattingRoom;
import com.example.runningweb.config.redis.RedisSubscriberService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.nio.channels.Channel;
import java.util.*;


@Component
@RequiredArgsConstructor
public class ChatRoomRepository {

    //redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private static final String USER_COUNT = "USER_COUNT";
    private static final String ENTER_INFO = "ENTER_INFO";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChattingRoom> opsHashChatRoom; // (CHAT_ROOMS), (roomId(UUID), chatRoom) 형태로 저장
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo; // (chat_Room), (roomId, USERINFO)
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;


//    @PostConstruct
//    public void init() {
//        opsHashChatRoom = template.opsForHash();
//        hashOpsEnterInfo = template.opsForHash();
//
//    }

    //모든 채팅방 조회
    public List<ChattingRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public ChattingRoom findById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    // 채팅방 생성 : 서버간 공유를 위해 Redis에 저장한다.
    public ChattingRoom createChattingRoom(String name) {
        ChattingRoom chattingRoom = new ChattingRoom(name);
        opsHashChatRoom.put(CHAT_ROOMS, chattingRoom.getRoomId(), chattingRoom);
        return chattingRoom;
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
