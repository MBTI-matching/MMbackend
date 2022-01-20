package com.sparta.mbti.repository;

import com.sparta.mbti.model.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    Boolean existsByHostIdAndGuestId(Long hostId, Long guestId);

    Matching findByHostIdAndGuestId(Long hostId, Long id);

    List<Matching> findAllByHostId(Long id);

    List<Matching> findAllByGuestId(Long id);

}
