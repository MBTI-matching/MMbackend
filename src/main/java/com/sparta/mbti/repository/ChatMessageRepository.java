package com.sparta.mbti.repository;

import com.sparta.mbti.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomIdOrderByIdDesc(Pageable pageable, String roomId);
    ChatMessage findFirstByRoomIdOrderByIdDesc(String roomId);
    Boolean existsByRoomId(String roomId);
}
