package com.sparta.mbti.model;

import com.sparta.mbti.dto.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

//    @OneToMany(cascade = CascadeType.ALL)
//    private final List<ChatMessage> messageList = new ArrayList<>();

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

    public void deleteGuestId() {
        this.guestId = 0L;
    }

    public void deleteHostId() {
        this.hostId = 0L;
    }

}