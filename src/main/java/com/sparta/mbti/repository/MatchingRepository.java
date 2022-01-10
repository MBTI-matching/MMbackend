package com.sparta.mbti.repository;

import com.sparta.mbti.model.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    Boolean existsByHostIdAndGuestId(Long hostId, Long guestId);
}
