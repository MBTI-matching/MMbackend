package com.sparta.mbti.repository;

import com.sparta.mbti.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAll();
    ChatRoom findByRoomId(String roomId);
}
