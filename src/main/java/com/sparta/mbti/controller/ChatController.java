package com.sparta.mbti.controller;

import com.sparta.mbti.dto.response.ChatMessageRequestDto;
import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatMessageRepository;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.jwt.JwtDecoder;
import com.sparta.mbti.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

// import 생략...

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageRequestDto message, @Header("token") String token) {
        chatService.sendChatMessage(message, token);
    }
}
