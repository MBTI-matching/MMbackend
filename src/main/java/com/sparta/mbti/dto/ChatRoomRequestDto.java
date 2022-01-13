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
    private Long guestId; // entity에 guestId가 long이라고 되어있어서 entity쪽에 맞춤

    @Column
    private String guestMbti;

    @Column
    private String guestNick;

    @Column
    private String guestImg;
}