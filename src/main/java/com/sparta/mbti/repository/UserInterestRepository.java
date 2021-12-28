package com.sparta.mbti.repository;

import com.sparta.mbti.model.User;
import com.sparta.mbti.model.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findAllByUser(User user);
}
