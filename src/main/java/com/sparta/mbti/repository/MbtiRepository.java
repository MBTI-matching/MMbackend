package com.sparta.mbti.repository;

import com.sparta.mbti.model.Mbti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MbtiRepository extends JpaRepository<Mbti, Long> {
    Optional<Mbti> findByMbti(String mbti);
}
