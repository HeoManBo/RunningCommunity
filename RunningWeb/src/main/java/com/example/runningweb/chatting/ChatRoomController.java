package com.example.runningweb.chatting;


import com.example.runningweb.chatting.Repository.ChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingRoom;
import com.example.runningweb.security.MemberUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 관리를 위한 컨트롤러
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    //채팅방 view
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    //채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChattingRoom> allRoom() {
        List<ChattingRoom> chatRooms = chatRoomRepository.findAllRoom();
        chatRooms.forEach(chattingRoom -> chattingRoom.
                setUserCount(chatRoomRepository.getUserCount(chattingRoom.getRoomId())));
        return chatRooms;
    }

    //채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChattingRoom createRoom(@RequestParam(name = "name") String roomName) {
        return chatRoomRepository.createChattingRoom(roomName);
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable(name = "roomId") String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    //특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChattingRoom roomInfo(@PathVariable(name = "roomId") String roomId) {
        return chatRoomRepository.findById(roomId);
    }



    // chatting 방에 접속한 유저의 정보를 반환함.
    @GetMapping("/user")
    @ResponseBody
    public String getUserName(@AuthenticationPrincipal MemberUserDetails userDetails){
        return userDetails.getMember().getNickname();
    }

}
