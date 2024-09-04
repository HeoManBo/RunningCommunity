package com.example.runningweb.service;

import com.example.runningweb.dto.CommentEvent;
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

    private final SseEmitters sseEmitters;
    private final ChannelTopic commentTopic;
    private final RedisTemplate<String, Object> template;


    public CommentEventListener(SseEmitters sseEmitters,
                                @Qualifier("commentTopic") ChannelTopic channelTopic,
                                RedisTemplate<String, Object> redisTemplate) {
        this.sseEmitters = sseEmitters;
        this.commentTopic = channelTopic;
        this.template = redisTemplate;
    }

    @EventListener
    @Async("commentAsyncExecutors")
    public void sendCommentEvent(CommentEvent commentEvent) throws InterruptedException {
        //sseEmitters.sendNewCommentCount(commentEvent.getBoardId());
        log.info("토픽 = {}, 전송하는 게시판 번호 = {}", commentTopic.getTopic(), commentEvent.getBoardId());
        template.convertAndSend(commentTopic.getTopic(), commentEvent.getBoardId());
    }


}
