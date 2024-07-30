package com.example.runningweb.chatting;


import com.example.runningweb.chatting.Repository.RedisChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingMessage;
import com.example.runningweb.chatting.service.ChatService;
import com.example.runningweb.domain.Member;
import com.example.runningweb.security.MemberUserDetails;
import com.example.runningweb.util.Utils;
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

    private final ChatService chatService;

    /**
     * /pub/chat/message 로 들어온 메세지를 처리
     * principal : websocket handshake시 시점에 로그인 유저 정보
     */
    @MessageMapping("/chat/message")
    public void message(ChattingMessage message, Principal principal) {
        String username = getUsername(principal);
        message.setSender(username);

        Member member = Utils.extractLoginMember(principal);
        if(member == null) throw new IllegalArgumentException("잘못된 메세지 전송");
        // redis로 메세지 발행
        chatService.sendChatMessage(message, member);
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
