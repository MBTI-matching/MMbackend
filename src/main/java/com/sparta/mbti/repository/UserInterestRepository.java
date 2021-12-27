package com.sparta.mbti.repository;

import com.sparta.mbti.model.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
}
