package com.sparta.mbti.dto.response;

import com.sparta.mbti.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponseDto {
    private String roomId;
    private Long guestId;
    private String guestMbti;
    private String guestNick;
    private String guestImg;
    private String lastMessage;
    private String messageTime;
    private ChatMessage.MessageType messageType;
}