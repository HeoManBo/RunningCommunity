package com.example.runningweb.service;

import com.example.runningweb.dto.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final SseEmitters sseEmitters;


    @EventListener
    @Async("commentAsyncExecutors")
    public void sendCommentEvent(CommentEvent commentEvent) throws InterruptedException {
        sseEmitters.sendNewCommentCount(commentEvent.getBoardId());
    }


}
