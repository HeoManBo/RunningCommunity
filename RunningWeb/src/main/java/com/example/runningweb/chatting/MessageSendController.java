package com.example.runningweb.chatting;


import com.example.runningweb.chatting.Repository.ChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingMessage;
import com.example.runningweb.chatting.domain.MessageType;
import com.example.runningweb.config.redis.RedisPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MessageSendController {

    private final RedisPublisherService publisher;
    private final ChatRoomRepository chatRoomRepository;

    // /pub/chat/message 로 발행된 문자를 처리하여 구독자에게 전송한다.
    // 구독자는 웹단에서 /sub/chat/room/{roomId}를 구독하면 된다.
//    @MessageMapping("/chat/message")
//    public void message(ChattingMessage message){
//        log.info("채팅방 입장 타입 = {}", message.getType().toString());
//        if (message.getType().equals(MessageType.ENTER)) {
//            message.setMessage(message.getSender() + "님이 입장하였습니다."); //입장시 메세지 설정
//        }
//        log.info("전송된 문자 = {}", message.getMessage());
//        // /sub/chat/room/{roomId} 채팅방에 있는 사람들에게 메세지 보내기
//        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
//    }

    @MessageMapping("/chat/message")
    public void message(ChattingMessage message) {
        if (message.getType().equals(MessageType.ENTER)) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // Websocket 으로 전송된 메세지가 redis 로 발행된다
        publisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
