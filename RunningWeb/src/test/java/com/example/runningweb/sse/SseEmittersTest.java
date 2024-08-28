package com.example.runningweb.sse;

import com.example.runningweb.service.SseEmitters;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SseEmittersTest {

    private SseEmitters sseEmitters = new SseEmitters();

    @Test
    public void 동시에_boardId에_100명접속() throws InterruptedException {
        //given : 100명의 유저가 동시에 1번 게시물 접속 시도 상황
        final int THREAD_COUNT = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        final String boardId = "1";

        //then : sse 구독
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                sseEmitters.add(new SseEmitter(), boardId);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        // 100개가 되어야함.
        int size = sseEmitters.size(boardId);
        Assertions.assertThat(size).isEqualTo(1000);
    }

}
