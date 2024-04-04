package com.example.runningweb.chatting.Repository;

import com.example.runningweb.chatting.domain.ChattingRoom;
import com.example.runningweb.config.redis.RedisSubscriberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.nio.channels.Channel;
import java.util.*;

// Redis로 pub/sub 구현 --> 멀티 서버 환경에서도 돌아갈 수 있도록
//@Repository
@Component
@RequiredArgsConstructor
public class ChatRoomRepository {

    private final RedisMessageListenerContainer listener; // topic에 발행되는 메세지를 처리할 Listener
    private final RedisSubscriberService subscriberService; // 구독자에게 메세지 전송
    //redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> template;
    private HashOperations<String, String, ChattingRoom> opsHashChatRoom; // (CHAT_ROOMS), (roomId(UUID), chatRoom) 형태로 저장
    //채팅방의 대화 메시지를 발행할 topic 정보 topic을 roomdId로 찾을 수 있게함
    private Map<String, ChannelTopic> topcis;

    @PostConstruct
    public void init() {
        topcis = new HashMap<>();
        opsHashChatRoom = template.opsForHash();
    }

    public List<ChattingRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChattingRoom findById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    public ChattingRoom createChattingRoom(String name) {
        ChattingRoom chattingRoom = new ChattingRoom(name);
        opsHashChatRoom.put(CHAT_ROOMS, chattingRoom.getRoomId(), chattingRoom);
        return chattingRoom;
    }


    /**
     * 채팅방 입장 : redis에 topic을 만들고 listener 설정
     */
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topcis.get(roomId);
        if (topic == null) { //채팅방을 생성하는 경우
            topic = new ChannelTopic(roomId); // roomId에 대한 topic 생성
            listener.addMessageListener(subscriberService, topic); // topic에 대한 구독서비스
            topcis.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId) {
        return topcis.get(roomId);
    }


}
