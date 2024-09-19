package com.example.runningweb.config.redis;


import com.example.runningweb.dto.CommentEvent;
import com.example.runningweb.service.SseEmitters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCommentSubscriberService {

    private final SseEmitters sseEmitters;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendNewCommentAlert(String publishMessage) throws JsonProcessingException {
        log.info("전송된 메세지 : {}", publishMessage);
        CommentEvent commentEvent = objectMapper.readValue(publishMessage, CommentEvent.class);
        sseEmitters.sendNewCommentCount(commentEvent.getBoardId(), commentEvent.getUserId());
    }

}
