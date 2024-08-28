package com.example.runningweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class SseEmitters {

//    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private final Map<String, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

//    private static final AtomicLong counter = new AtomicLong();

//    public SseEmitter add(SseEmitter emitter) {
//        this.emitters.add(emitter);
//        emitter.onCompletion(() -> {
//            log.info("onCompletion callback");
//            this.emitters.remove(emitter); // 만료되면 리스트에서 삭제
//        });
//        emitter.onTimeout(() -> {
//            log.info("onTimeout callback");
//            emitter.complete();
//        });
//
//        return emitter;
//    }

    public SseEmitter add(SseEmitter emitter, String boardId) {
        emitterMap.computeIfAbsent(boardId, key -> new CopyOnWriteArrayList<SseEmitter>());
        List<SseEmitter> sseEmitters = emitterMap.get(boardId);
        sseEmitters.add(emitter);

        log.info("new emitter added: {}", emitter);
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            sseEmitters.remove(emitter);    // 만료되면 리스트에서 삭제
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    public void sendNewCommentCount(String boardId) {
        List<SseEmitter> sseEmitters = emitterMap.get(boardId);
        if (sseEmitters == null || sseEmitters.size() == 0) {
            return;
        }
        log.info("ASDSADASDAD");
        sseEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("count")
                        .data(1));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }


//    public void count() {
//        long count = counter.incrementAndGet();
//        emitters.forEach(emitter -> {
//            try {
//                emitter.send(SseEmitter.event()
//                        .name("count")
//                        .data(count));
//                log.info("count = {}", count);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }

    public int size(String boardId){
        if (!emitterMap.containsKey(boardId)) {
            return 0;
        }
        return emitterMap.get(boardId).size();
    }
}
