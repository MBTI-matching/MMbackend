package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private Long hostId;

    @Column
    private Long guestId;

    @Column
    private String guestImg;

    @Column
    private String guestNick;

    @Column
    private String guestMbti;

    @Column
    private String roomId;

    public ChatRoom(Long hostId, Long guestId, String guestImg, String guestMbti, String guestNick, String roomId) {
        this.hostId = hostId;
        this.guestId = guestId;
        this.guestMbti = guestMbti;
        this.guestImg = guestImg;
        this.guestNick = guestNick;
        this.roomId = roomId;
    }
}