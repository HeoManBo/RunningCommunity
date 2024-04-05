package com.example.runningweb.config.redis;

import com.example.runningweb.chatting.domain.ChattingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 * 메세지 작성시 해당 메세지를 Redis Topic 에 발행하는 기능
 * Topic : 채팅방
 * pub : 메시지 전송
 * sub : 메세지 수신
 */

//@Service
@RequiredArgsConstructor
public class RedisPublisherService {

    private final RedisTemplate<String ,Object> redisTemplate;

    public void publish(ChannelTopic topic, ChattingMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}
