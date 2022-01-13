package com.sparta.mbti.dto;

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
    @Column
    private String guestId; // guestEmail로 바꿔쓰길 제의

    @Column
    private String guestMbti;

    @Column
    private String guestNick;

    @Column
    private String guestImg;
}