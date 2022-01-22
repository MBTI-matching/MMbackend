package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, QUIT, EMO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @Column
    private String roomId;

    @Column
    private String message;

    @Column
    private Long senderId;

    @Column
    private String senderName;

    @Column
    private String senderNick;

    @Column
    private String senderImg;

    @Column
    private String date;

}