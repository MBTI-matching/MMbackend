package com.sparta.mbti.controller;

import com.sparta.mbti.dto.ChatMessageRequestDto;
import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatMessageRepository;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.jwt.JwtDecoder;
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

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtDecoder jwtDecoder;
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageRequestDto message, @Header("token") String token) {

        String username = jwtDecoder.decodeUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다.")
        );
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage("[알림] " + user.getNickname() + " 님이 입장하셨습니다.");
        }else if(ChatMessage.MessageType.QUIT.equals(message.getType())){
            message.setMessage("[알림] " + user.getNickname() + "님이 퇴장하셨습니다.");
        }

        message.setSenderId(user.getId());
        message.setSenderImg(user.getProfileImage());
        message.setSenderNick(user.getNickname());
        message.setSenderName(user.getUsername());

        // Websocket에 발행된 메시지를 redis로 발행(publish)

        ChatMessage newMessage = ChatMessage.builder()
                .message(message.getMessage())
                .roomId(message.getRoomId())
                .type(message.getType())
                .date(message.getDate())
                .senderId(message.getSenderId())
                .senderImg(message.getSenderImg())
                .senderName(message.getSenderName())
                .senderNick(message.getSenderNick())
                .build();
        if (ChatMessage.MessageType.TALK.equals(message.getType()))
            chatMessageRepository.save(newMessage);

        redisTemplate.convertAndSend(channelTopic.getTopic(), message);

    }
}
