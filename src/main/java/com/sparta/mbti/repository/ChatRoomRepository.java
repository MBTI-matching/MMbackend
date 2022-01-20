package com.sparta.mbti.repository;

import com.sparta.mbti.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByHostIdOrGuestId(Long hostId, Long guestId);
    Boolean existsByHostId(Long hostId);
    Boolean existsByGuestId(Long guestId);
    List<ChatRoom> findAllByHostId(Long hostId);
    List<ChatRoom> findAllByGuestId(Long guestId);
    Optional<ChatRoom> findByRoomId(String roomId);
    Boolean existsByHostIdAndGuestId(Long hostId, Long guestId);
}
