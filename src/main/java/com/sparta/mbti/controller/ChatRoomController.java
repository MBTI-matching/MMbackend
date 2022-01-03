package com.sparta.mbti.controller;

import com.sparta.mbti.dto.ChatMessageDto;
import com.sparta.mbti.dto.ChatMessageResponseDto;
import com.sparta.mbti.dto.ChatRoomDto;
import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController{

    private final ChatRoomService chatRoomService;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public List<ChatRoomDto> room(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findAllRoom(userDetails.getUser().getId());
    }

    // 채팅방 생성
    // guestId가 채팅방 제목
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam Long guestId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createChatRoom(guestId, userDetails.getUser().getId());
    }

//    // 채팅방 입장 화면
//    @GetMapping("/room/enter/{roomId}")
//    public String roomDetail(Model model, @PathVariable String roomId) {
//        model.addAttribute("roomId", roomId);
//        return "testroomdetail";       //aws
//        //return "/chat/roomdetail";    //로컬호스트
//    }

//    // 특정 채팅방 조회
//    @GetMapping("/room/{roomId}")
//    @ResponseBody
//    public ChatRoom roomInfo(@PathVariable String roomId) {
//        return chatRoomService.findRoomById(roomId);
//    }

    //채팅방 입장 시 메세지 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatMessageResponseDto> readAllMessage(@PathVariable String roomId){
        ChatMessageResponseDto responseDto= chatRoomService.readAllMessage(roomId);
        return ResponseEntity.ok().body(responseDto);
    }
}