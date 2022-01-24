package com.sparta.mbti.controller;

import com.sparta.mbti.dto.response.ChatMessageResponseDto;
import com.sparta.mbti.dto.request.ChatRoomRequestDto;
import com.sparta.mbti.dto.response.ChatRoomResponseDto;
import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 채팅방 입장 시 메세지 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessageResponseDto>> readAllMessage(@RequestParam int page,
                                                                       @RequestParam int size, @PathVariable String roomId){

        Pageable pageable = PageRequest.of(page, size);

        List<ChatMessageResponseDto> responseDto = chatRoomService.readAllMessage(pageable, roomId);
        return ResponseEntity.ok().body(responseDto);
    }

    // 채팅방 나가기
    @PutMapping("/room/{roomId}")
    public void quitRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String roomId){
        chatRoomService.exitChatRoom(userDetails.getUser().getId(), roomId);
    }

    // 방 삭제
    @DeleteMapping("/room/{roomId}")
    public void deleteRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String roomId){
        chatRoomService.deleteChatRoom(userDetails.getUser().getId(), roomId);
    }
}