package com.example.runningweb.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * 기존의 topic은 채팅방 생성시 무조건 새로운 Topic을 만들고 redislistener를 연결했어야 함
 * 이는 불필요한 자원을 낭비하는 단점이 있어 channneltopic과 redislistener룰 하나로 통합함.
 */

@Configuration
public class RedisConfigV2 {

    //단일 Topic 사용
    @Bean
    public ChannelTopic channelTopic(){
        return new ChannelTopic("chatroom");
    }

    /**
     * Redis의 pub/sub 메세지를 처리하는 Listener
     * 스프링 부튼 기본적으로 factory를 lettuce를 제공함.
     * Container providing asynchronous behaviour for Redis message listeners.
     * Handles the low level details of listening, converting and message dispatching.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory,
                                                                       MessageListenerAdapter adapter,
                                                                       ChannelTopic topic){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(adapter, topic);
        return container;
    }

    //실제 메시지를 처리하는 adaptor 설정 , publisher 역할
    @Bean
    public MessageListenerAdapter adapter(RedisSubscriberService subscriberService){
        //메시지가 발행됐을 때 subscriberService 의 onMessage 실행됨
        return new MessageListenerAdapter(subscriberService, "sendMessage");
    }

    /**
     * 애플리케이션에서 사용할 redisTemplate 사용
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

}
