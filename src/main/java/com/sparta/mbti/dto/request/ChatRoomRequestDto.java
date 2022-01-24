package com.sparta.mbti.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequestDto {
    private String guestEmail;
    private String guestMbti;
    private String guestNick;
    private String guestImg;
}