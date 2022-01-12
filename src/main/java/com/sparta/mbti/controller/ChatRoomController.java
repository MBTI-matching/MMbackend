package com.sparta.mbti.controller;

import com.sparta.mbti.dto.ChatMessageResponseDto;
import com.sparta.mbti.dto.ChatRoomRequestDto;
import com.sparta.mbti.dto.ChatRoomResponseDto;
import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController{

    private final ChatRoomService chatRoomService;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public List<ChatRoomResponseDto> room(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findAllRoom(userDetails.getUser().getId());
    }

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        return chatRoomService.createChatRoom(userDetails.getUser().getId(), chatRoomRequestDto);
    }
    //채팅방 입장 시 메세지 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessageResponseDto>> readAllMessage(@PathVariable String roomId){
        List<ChatMessageResponseDto> responseDto = chatRoomService.readAllMessage(roomId);
        return ResponseEntity.ok().body(responseDto);
    }
}