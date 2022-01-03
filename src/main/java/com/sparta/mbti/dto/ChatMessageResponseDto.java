package com.sparta.mbti.dto;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    private Long hostId;
    private Long guestId;
    private String roomId;
    private List<ChatMessageDto> chatMessageDtoList;
}