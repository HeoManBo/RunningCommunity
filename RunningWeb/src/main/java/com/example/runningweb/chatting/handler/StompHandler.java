package com.example.runningweb.chatting.handler;

import com.example.runningweb.chatting.Repository.ChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingMessage;
import com.example.runningweb.chatting.domain.MessageType;
import com.example.runningweb.chatting.service.ChatService;
import com.example.runningweb.security.MemberUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

//메시지 송,수신전 가로채셔 체크됨. 인터셉터의 역할
@RequiredArgsConstructor
@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

    private final ChatRoomRepository roomRepository;
    private final ChatService chatService;

    //전송하기 전 처리
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Stomp 프로토콜 헤더에 접근할 수 있도록 설정
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String username = parseUsername(accessor); // 로그인중인 유저 이름 추출
        if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 구독 요청
            // 구독 중인 Roomid를 추출한다
            String roomId = chatService.getRoomId(Optional.ofNullable((String)message.getHeaders().get("simpDestination"))
                    .orElse("InvalidRoomId"));
            //채팅방에 들어온 클라이언트의 sessionId를 roomId와 매핑한다. -> 특정 세션이 어느 채팅방에 있는지 알기 이해
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            roomRepository.setUserEnterInfo(sessionId, roomId);
            roomRepository.addUserCount(roomId);
            log.info("CONNECT USER = {}, CONNECT ROOM = {}", username, roomId);
            chatService.sendChatMessage(ChattingMessage.builder().sender(username).roomId(roomId).type(MessageType.ENTER).build());
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            //채팅방에 들어온 클라이언트의 sessionId를 roomId와 매핑한다. -> 특정 세션이 어느 채팅방에 있는지 알기 이해
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = roomRepository.getUserEnterRoomId(sessionId);

            roomRepository.setUserEnterInfo(sessionId, roomId);
            roomRepository.minusUserCount(roomId);

            // 입장 메시지를 발송핸다 (redis publish)
            chatService.sendChatMessage(ChattingMessage.builder().sender(username).roomId(roomId).type(MessageType.QUIT).build());
            log.info("DISCONNECT USER = {}, CONNECT ROOM = {}", username, roomId);
            roomRepository.removeUserEnterInfo(sessionId);
        }

        return message;
    }

    // Header에 simpUser 헤더의 담긴 user 정보룰 추출한다.
    private String parseUsername(StompHeaderAccessor accessor) {
        Object header = accessor.getHeader("simpUser");
        if(header instanceof UsernamePasswordAuthenticationToken token) {
            if(token.getPrincipal() instanceof MemberUserDetails details){
                return details.getMember().getNickname();
            }
        }

        return "Unknown User";
    }
}
