package com.example.runningweb.chatting.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@EnableWebSocketMessageBroker // stomp 사용
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChattingConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 메시지 수신처리. 구독자들은 /sub/..으로 메세지를 수신함
        config.setApplicationDestinationPrefixes("/pub"); // 메시지 발행(전송)처리. 구독자들에게 /pub/...으로 전송
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 채팅서버 stomp websocket 연결 endpoint (접속 주소)
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*").withSockJS();
    }
}