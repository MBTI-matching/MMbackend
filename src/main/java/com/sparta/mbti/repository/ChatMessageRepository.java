package com.sparta.mbti.repository;

import com.sparta.mbti.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomIdOrderByTimenowDesc(String roomId);

    ChatMessage findFirstByRoomIdOrderByTimenowDesc(String roomId);
}
