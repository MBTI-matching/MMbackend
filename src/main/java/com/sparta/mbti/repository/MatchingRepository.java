package com.sparta.mbti.repository;

import com.sparta.mbti.model.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    Boolean existsByHostIdAndGuestId(Long hostId, Long guestId);

    Optional<Matching> findByHostIdAndGuestId(Long hostId, Long id);
}
