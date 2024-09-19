package com.example.runningweb.controller;

import com.example.runningweb.service.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SSEController {

    private final SseEmitters sseEmitters;

    @GetMapping(value = "/connect/{boardId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@PathVariable("boardId") String boardId) {
        SseEmitter emitter = new SseEmitter(Duration.ofMinutes(1).toMillis()); // 10분동안 연결 후 재접속
        sseEmitters.add(emitter, boardId);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("conencted!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

//    @GetMapping("/count")
//    public ResponseEntity<Void> count(){
//        sseEmitters.sendNewCommentCount("324");
//        return ResponseEntity.ok().build();
//    }


}
