package com.example.runningweb.service;

import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.AttendingRoomRequest;
import com.example.runningweb.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChattingRoomService {

    private final ChattingRoomRepository roomRepository;


    public Long createChattingRoom(String title, String uuid, Member member){
        ChattingRoom chattingRoom = ChattingRoom.builder()
                .member(member)
                .region("ASDF")
                .title(title)
                .uuid(uuid)
                .build();
        roomRepository.save(chattingRoom);
        return chattingRoom.getChattingRoomId();
    }

}
