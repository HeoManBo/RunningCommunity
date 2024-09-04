package com.example.runningweb.config.redis;


import com.example.runningweb.service.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCommentSubscriberService {

    private final SseEmitters sseEmitters;

    public void sendNewCommentAlert(String publishMessage) {
        log.info("전송된 메세지 : {}", publishMessage);
        String boardId = publishMessage.replaceAll("\"", "");
        sseEmitters.sendNewCommentCount(boardId);
    }

}
