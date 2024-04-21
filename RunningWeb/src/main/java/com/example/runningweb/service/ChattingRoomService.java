package com.example.runningweb.service;

import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.Member;
import com.example.runningweb.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChattingRoomService {

    private final ChattingRoomRepository roomRepository;

    public Long createChattingRoom(String title, String uuid, String region, Member member){
        ChattingRoom chattingRoom = ChattingRoom.builder()
                .member(member)
                .region(region)
                .title(title)
                .uuid(uuid)
                .build();
        roomRepository.save(chattingRoom);
        return chattingRoom.getChattingRoomId();
    }

}
