package com.example.runningweb.chatting.service;


import com.example.runningweb.chatting.Repository.RedisChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingMessage;
import com.example.runningweb.chatting.domain.MessageType;
import com.example.runningweb.domain.Member;
import com.example.runningweb.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChannelTopic topic;
    //private final RedisTemplate<String, Object> template;
    private final RedisTemplate<String, Object> template;
    private final RedisChatRoomRepository roomRepository;
    private final MessageService messageService;

    /**
     * destination 정보에서 roomId ㅊ출
     */
    public String getRoomId(String destination)
    {
        int lastIndex = destination.lastIndexOf('/');
        if(lastIndex != -1){
            return destination.substring(lastIndex+1); //roomId 추출
        }else return "";
    }

    /**
     * 채팅방에 메세지 전송 (pub)
     */
    public void sendChatMessage(ChattingMessage chattingMessage, Member member) {
        chattingMessage.setUserCount(roomRepository.getUserCount(chattingMessage.getRoomId()));
        if (chattingMessage.getType().equals(MessageType.ENTER)) {
            chattingMessage.setMessage(chattingMessage.getSender() + "님이 입장하였습니다.");
            chattingMessage.setSender("[알림] ");
        } else if (chattingMessage.getType().equals(MessageType.QUIT)) {
            chattingMessage.setMessage(chattingMessage.getSender() + "님이 방에서 퇴장했습니다.");
            chattingMessage.setSender("[알림] ");
        }

        messageService.saveMessage(chattingMessage, member);
        //메시지 전송
        template.convertAndSend(topic.getTopic(), chattingMessage);
    }
}
