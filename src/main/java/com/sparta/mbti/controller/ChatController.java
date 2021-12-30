package com.sparta.mbti.controller;

import com.sparta.mbti.config.RedisConfig;
import com.sparta.mbti.dto.ChatMessageDto;
import com.sparta.mbti.security.jwt.JwtTokenUtils;
import com.sparta.mbti.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

// import 생략...

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final JwtTokenUtils jwtTokenUtils;
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("token") String token) {
        String nickname = jwtTokenUtils.getUserNameFromJwt(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(nickname);
        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        if (ChatMessageDto.MessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        //redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
