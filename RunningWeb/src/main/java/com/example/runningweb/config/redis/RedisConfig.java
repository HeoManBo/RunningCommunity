package com.example.runningweb.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * 기존의 topic은 채팅방 생성시 무조건 새로운 Topic을 만들고 redislistener를 연결했어야 함
 * 이는 불필요한 자원을 낭비하는 단점이 있어 channneltopic과 redislistener룰 하나로 통합함.
 */

@Configuration
public class RedisConfig {

    //채팅 메세지를 구독하는 Topic
    @Bean
    public ChannelTopic chattingMessageTopic(){
        return new ChannelTopic("chatroom");
    }

    //댓글 갱신을 구독하는 topic
    @Bean
    public ChannelTopic commentTopic(){
        return new ChannelTopic("comment");
    }

    /**
     * Redis의 pub/sub 메세지를 처리하는 Listener
     * 스프링 부튼 기본적으로 factory를 lettuce를 제공함.
     * Container providing asynchronous behaviour for Redis message listeners.
     * Handles the low level details of listening, converting and message dispatching.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory,
                                                                       @Qualifier("adapter") MessageListenerAdapter adapter,
                                                                       @Qualifier("commentAdapter") MessageListenerAdapter commentAdapter,
                                                                       @Qualifier("chattingMessageTopic") ChannelTopic chatTopic,
                                                                       @Qualifier("commentTopic") ChannelTopic commentTopic) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setThreadNamePrefix("redisThread-");
        executor.setQueueCapacity(1024);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //못보낼 경우 직접 전송하도록
        executor.initialize();

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(adapter, chatTopic);
        container.addMessageListener(commentAdapter, commentTopic);
        container.setTaskExecutor(executor);

        return container;
    }

    //실제 메시지를 처리하는 adaptor 설정 , publisher 역할
    @Bean
    public MessageListenerAdapter adapter(RedisSubscriberService subscriberService){
        //메시지가 발행됐을 때 subscriberService 의 sendMessage 실행됨
        return new MessageListenerAdapter(subscriberService, "sendMessage");
    }

    @Bean
    public MessageListenerAdapter commentAdapter(RedisCommentSubscriberService redisCommentSubscriberService) {
        return new MessageListenerAdapter(redisCommentSubscriberService, "sendNewCommentAlert");
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
