package com.sparta.mbti.service;

import com.sparta.mbti.dto.response.ChatMessageRequestDto;
import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatMessageRepository;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtDecoder jwtDecoder;

    public void sendChatMessage(ChatMessageRequestDto message, String token) {
        String username = jwtDecoder.decodeUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다.")
        );
        message.setSenderId(user.getId());
        message.setSenderImg(user.getProfileImage());
        message.setSenderNick(user.getNickname());
        message.setSenderName(user.getUsername());

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage("[알림] " + user.getNickname() + " 님이 입장하셨습니다.");
        }else if(ChatMessage.MessageType.QUIT.equals(message.getType())){
            message.setMessage("[알림] " + user.getNickname() + "님이 퇴장하셨습니다.");
        }else if(ChatMessage.MessageType.EMO.equals(message.getType())){
            message.setMessage("https://bizchemy-bucket-s3.s3.ap-northeast-2.amazonaws.com/emoticon/emoticon_"+message.getMessage()+".png");
            ChatMessage emoMessage = ChatMessage.builder()
                    .message(message.getMessage())
                    .roomId(message.getRoomId())
                    .type(message.getType())
                    .date(message.getDate())
                    .senderId(message.getSenderId())
                    .senderImg(message.getSenderImg())
                    .senderName(message.getSenderName())
                    .senderNick(message.getSenderNick())
                    .build();

            chatMessageRepository.save(emoMessage);

        }
        else {
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

            chatMessageRepository.save(newMessage);
        }
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
