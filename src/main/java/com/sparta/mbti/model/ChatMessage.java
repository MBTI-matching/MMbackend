package com.sparta.mbti.model;

import com.sparta.mbti.dto.ChatMessageDto;
import com.sparta.mbti.service.UserService;
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
public class ChatMessage extends Timestamped {

    public enum MessageType {
        ENTER, TALK
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

    @ManyToOne
    private User sender;

}