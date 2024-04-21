package com.example.runningweb.chatting;


import com.example.runningweb.chatting.Repository.RedisChatRoomRepository;
import com.example.runningweb.chatting.domain.RedisChattingRoom;
import com.example.runningweb.domain.EnteredRoom;
import com.example.runningweb.dto.AttendingRoomRequest;
import com.example.runningweb.dto.MessageHistoryResponse;
import com.example.runningweb.repository.ChattingRoomRepository;
import com.example.runningweb.security.MemberUserDetails;
import com.example.runningweb.service.ChattingRoomService;
import com.example.runningweb.service.EnteredRoomService;
import com.example.runningweb.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅방 관리를 위한 컨트롤러
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatRoomController {

    private final RedisChatRoomRepository chatRoomRepository;
    private final EnteredRoomService roomService;
    private final MessageService messageService;

    //채팅방 view
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    //채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<RedisChattingRoom> allRoom(@RequestParam(name = "region", required = false) String region) {
        log.info("선택한 지역 = {}", region);
        List<RedisChattingRoom> chatRooms = chatRoomRepository.findAllRoom();
        chatRooms.forEach(redisChattingRoom -> redisChattingRoom.
                setUserCount(chatRoomRepository.getUserCount(redisChattingRoom.getRoomId())));
        if (region == null) {
            return chatRooms;
        } else {
            List<RedisChattingRoom> filter = chatRooms.stream().filter(cr -> cr.getRegion().contains(region))
                    .toList();
            filter.forEach(redisChattingRoom -> redisChattingRoom.
                    setUserCount(chatRoomRepository.getUserCount(redisChattingRoom.getRoomId())));
            return filter;
        }
    }

    //채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public RedisChattingRoom createRoom(@RequestParam(name = "name") String roomName,
                                        @RequestParam(name = "region") String region,
                                        @AuthenticationPrincipal MemberUserDetails memberUserDetails) {
        return chatRoomRepository.createChattingRoom(roomName, region, memberUserDetails.getMember());
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
    public RedisChattingRoom roomInfo(@PathVariable(name = "roomId") String roomId) {
        return chatRoomRepository.findById(roomId);
    }


    // chatting 방에 접속한 유저의 정보를 반환함.
    // 만약 입장한 채팅방이라면 nickname + 나갔던 시간을 반환한다.
    // --> 메세지를 다시 가져오기 위해
    @GetMapping("/user")
    @ResponseBody
    public String getUserName(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return userDetails.getMember().getNickname();
    }


    // 현재 유저가 참여 중인 채팅방을 반환한다.
    @ResponseBody
    @GetMapping("/attend")
    public List<AttendingRoomRequest> attendingRoom(@AuthenticationPrincipal MemberUserDetails memberUserDetails) {
        return roomService.findAttendingRoom(memberUserDetails.getMember());
    }


    //현재 채팅방의 참여한 유저들의 닉네임을 반환한다.
    @ResponseBody
    @GetMapping("/participants")
    public List<String> getParticipants(@RequestParam("roomId") String roomId) {
        return roomService.getParticipants(roomId);
    }

    @ResponseBody
    @GetMapping("/historyMessage")
    public List<MessageHistoryResponse> getMessageHistory(@RequestParam("roomId") String roomId,
                                                          @AuthenticationPrincipal MemberUserDetails memberUserDetails) {
        log.info("getMessageHistory = {}", roomId);
        EnteredRoom enteredRoom = roomService.getEnterHistory(roomId, memberUserDetails.getMember());
        // 들어간 기록이 없다면 빈 리스트 반환
        if(enteredRoom == null) {
            return new ArrayList<>();
        }

        //메세지 가져오기, 이때 해당 유저가 나간 시간을 파리미터로 더 한다.
        return messageService.getMessages(roomId, enteredRoom.getUpdatedAt());
    }

}
