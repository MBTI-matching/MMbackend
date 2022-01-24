package com.sparta.mbti.dto.response;

import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.User;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private Long senderId;
    private String senderImg;
    private String senderName;
    private String message;
    private String senderNick;
    private String date;
}
