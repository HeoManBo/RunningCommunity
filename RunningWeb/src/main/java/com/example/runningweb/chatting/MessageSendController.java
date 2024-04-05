package com.example.runningweb.chatting;


import com.example.runningweb.chatting.Repository.ChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingMessage;
import com.example.runningweb.chatting.service.ChatService;
import com.example.runningweb.security.MemberUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MessageSendController {

//    private final RedisPublisherService publisher;
//    private final ChatRoomRepository chatRoomRepository;

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


    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * /pub/chat/message 로 들어온 메세지를 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChattingMessage message, Principal principal) {
        String username = getUsername(principal);

        message.setSender(username);
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));

        // redis로 발행
        chatService.sendChatMessage(message);
    }


    private String getUsername(Principal principal) {
        String username = null;
        if(principal instanceof UsernamePasswordAuthenticationToken token){
            if(token.getPrincipal() instanceof UserDetails userDetails){
                username = ((MemberUserDetails) userDetails).getMember().getNickname();
            }
        }
        if(username == null){
            username = "Unknown User";
        }
        return username;
    }

}
