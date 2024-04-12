package com.example.runningweb.service;

import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.EnteredRoom;
import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.AttendingRoomRequest;
import com.example.runningweb.repository.ChattingRoomRepository;
import com.example.runningweb.repository.EnteredRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnteredRoomService {

    private final EnteredRoomRepository enteredRoomRepository;
    private final ChattingRoomRepository roomRepository;

    // 채팅방 입장 처리
    public void enterRoom(String roomId, Member member) {
        ChattingRoom room = findByRoomId(roomId);
        EnteredRoom enteredRoom = EnteredRoom
                .builder()
                .chattingRoom(room)
                .member(member).build();

        enteredRoomRepository.save(enteredRoom);
    }


    // 기본적으로 채팅방 참여시 ws 연결 후 roomId로 subscribe 요청을 날림
    // 그러면 입장한 기록이 있는지 확인하고, 없다면 참여 알림을 보냄
    @Transactional(readOnly = true)
    public boolean checkAlreadyInRoom(String roomId, Member member) {
        ChattingRoom room = findByRoomId(roomId);
        boolean isIn = enteredRoomRepository.existsByChattingRoomAndMember(room, member);

        return isIn;
    }


    // 퇴장 처리
    @Transactional
    public int exitRoom(String roomId, Member member) {
        ChattingRoom room = findByRoomId(roomId);
        int result = enteredRoomRepository.exitRoom(room, member);
        return result;
    }

    //퇴장 저장 시간 저장
    @Transactional
    public void saveExitTime(String roomId, Member member) {
        ChattingRoom room = findByRoomId(roomId);
        enteredRoomRepository.saveExitTime(room, member);
    }

    public ChattingRoom findByRoomId(String roomId){
        return roomRepository.findByUuid(roomId);
    }

    @Transactional(readOnly = true)
    public List<AttendingRoomRequest> findAttendingRoom(Member member) {

        //참여중인 방 정보 얻기
        List<EnteredRoom> attendingRoom = enteredRoomRepository.findAttendingRoom(member);
        List<ChattingRoom> list = attendingRoom.stream().map(EnteredRoom::getChattingRoom).toList();

        return list.stream()
                .map(l -> {
                    AttendingRoomRequest req = new AttendingRoomRequest(l.getUuid(), l.getTitle());
                    req.setUserCount(enteredRoomRepository.countInUser(l));
                    return req;
                }).toList();
    }

    @Transactional(readOnly = true)
    public List<String> getParticipants(String roomId) {
        ChattingRoom room = roomRepository.findByUuid(roomId);
        return enteredRoomRepository.findParticipants(room);
    }
}
