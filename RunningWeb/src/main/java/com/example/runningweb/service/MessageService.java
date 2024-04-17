package com.example.runningweb.service;

import com.example.runningweb.chatting.domain.ChattingMessage;
import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.Member;
import com.example.runningweb.domain.Message;
import com.example.runningweb.dto.MessageHistoryResponse;
import com.example.runningweb.repository.ChattingRoomRepository;
import com.example.runningweb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChattingRoomRepository roomRepository;


    //전송한 메세지 저장
    public void saveMessage(ChattingMessage chattingMessage, Member member) {
        // 추후 채팅방 기록을 redis나 map으로 저장해서 db 접근 줄이기
        ChattingRoom room = roomRepository.findByUuid(chattingMessage.getRoomId());

        Message message = Message.builder()
                .message(chattingMessage.getMessage())
                .writer(member)
                .chattingRoom(room)
                .build();

        Message savedMessage = messageRepository.save(message);
        if (savedMessage.getId() == null) {
            log.error("채팅 메세지 저장 실패 저장 메세지 = {}, 작성자 ={}, 작성 방 번호={}",
                    message.getMessage(), member.getUsername(), room.getUuid());
        }
    }

    // roomId 방에 저장된 모든 메세지를 가져온다.
    @Transactional(readOnly = true)
    public List<MessageHistoryResponse> getMessages(String roomId, LocalDateTime exitTime) {
        ChattingRoom room = roomRepository.findByUuid(roomId);

        List<Message> messages = messageRepository.findByChattingRoom(room);
        messages.sort(Comparator.comparing(Message::getUpdatedAt)); // 오래된 메시지 부터 정렬

        List<MessageHistoryResponse> result = new ArrayList<>();
        boolean first = true;
        for(Message message : messages){
            if(message.getCreatedAt().isAfter(exitTime)){
                if(first){
                    first = false;
                    result.add(new MessageHistoryResponse("-----------", "퇴장 이후 전송된 메세지---------------"));
                }
            }
            result.add(new MessageHistoryResponse(message.getWriter().getNickname(), message.getMessage()));
        }

        //다시 역순으로..????
        Collections.reverse(result);

        return result;
    }
}
