package com.example.runningweb.config.redis;

import com.example.runningweb.chatting.domain.ChattingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriberService {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     *  Callback for processing received objects through Redis.
     *  redis를 통해 받은 메세지를 수신함.
     *  수신된 메세지는 {json 형태로 chattigMessage의 json 화된 형태임 }
     *  메시지가 수신되면 sendMessage 실행
     */
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        try{
//            //전송된 메세지를 역직렬화
//            String publishedMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
//            // ChattigMessage 로 매핑
//            ChattingMessage chattingMessage = objectMapper.readValue(publishedMessage, ChattingMessage.class);
//            // 구독자에게 발송
//            messagingTemplate.convertAndSend("/sub/chat/room/" + chattingMessage.getRoomId(), chattingMessage);
//        } catch (Exception e){
//            log.error(e.getMessage());
//        }
//    }

    //메시지가 발행되면 adapter가 sendMessage를 수행함
   public void sendMessage(String publishedMessage){
       try{
           //객채 매핑
           ChattingMessage chattingMessage = objectMapper.readValue(publishedMessage, ChattingMessage.class);
           // 구독한 클라이언트들에게 메세지 전송
           messagingTemplate.convertAndSend("/sub/chat/room/" + chattingMessage.getRoomId(), chattingMessage);
       }catch (Exception e){
           log.error(e.getMessage());
       }
   }


}
