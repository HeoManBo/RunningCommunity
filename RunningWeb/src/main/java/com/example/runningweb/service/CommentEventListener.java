package com.example.runningweb.service;

import com.example.runningweb.dto.CommentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventListener {

    private final ChannelTopic commentTopic;
    private final RedisTemplate<String, Object> template;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public CommentEventListener(@Qualifier("commentTopic") ChannelTopic channelTopic,
                                RedisTemplate<String, Object> redisTemplate) {
        this.commentTopic = channelTopic;
        this.template = redisTemplate;
    }

    @EventListener
    @Async("commentAsyncExecutors")
    public void sendCommentEvent(CommentEvent commentEvent) throws InterruptedException, JsonProcessingException {
        //sseEmitters.sendNewCommentCount(commentEvent.getBoardId());
        String s = objectMapper.writeValueAsString(commentEvent);
        log.info("토픽 = {}, 전송하는 게시판 번호 = {}", commentTopic.getTopic(), s);
        template.convertAndSend(commentTopic.getTopic(), commentEvent);
    }


}
