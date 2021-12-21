package com.sparta.mbti.repository;

import com.sparta.mbti.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByUserId(Long userId);

    //List<Interest> findAllByUserId(Long userId);
}
