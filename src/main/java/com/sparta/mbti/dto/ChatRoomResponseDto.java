package com.sparta.mbti.dto;

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
    @Column
    private String roomId;

    @Column
    private Long guestId;

    @Column
    private String guestMbti;

    @Column
    private String guestNick;

    @Column
    private String guestImg;

    @Column
    private String lastMessage;

    @Column
    private String messageTime;

    @Column
    private ChatMessage.MessageType messageType;
}