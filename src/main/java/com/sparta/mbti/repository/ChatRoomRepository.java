package com.sparta.mbti.repository;

import com.sparta.mbti.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByHostIdOrGuestId(Long hostId, Long guestId);
    ChatRoom findByRoomId(String roomId);
    Boolean existsByHostIdAndGuestId(Long hostId, Long guestId);
}
