package com.example.runningweb.chatting;


import com.example.runningweb.chatting.Repository.ChatRoomRepository;
import com.example.runningweb.chatting.domain.ChattingRoom;
import lombok.RequiredArgsConstructor;
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
        return chatRoomRepository.findAllRoom();
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


}
