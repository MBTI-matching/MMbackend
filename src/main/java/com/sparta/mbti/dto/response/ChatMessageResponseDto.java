package com.sparta.mbti.dto.response;

import com.sparta.mbti.model.ChatMessage;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    private ChatMessage.MessageType type;
    private Long senderId;
    private String senderImg;
    private String senderName;
    private String message;
    private String senderNick;
    private String date;
    private int totalMessage;
}