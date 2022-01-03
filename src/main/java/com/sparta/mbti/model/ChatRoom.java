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
    private String roomId;

    public ChatRoom(Long guestId, Long hostId) {
        this.hostId = hostId;
        this.roomId = UUID.randomUUID().toString();
        this.guestId = guestId;
    }
}