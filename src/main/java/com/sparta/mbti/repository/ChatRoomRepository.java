package com.sparta.mbti.repository;

import com.sparta.mbti.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByUser(User user);

    ChatRoom findByRoomId(String roomId);
}