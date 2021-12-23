package com.sparta.mbti.repository;

import com.sparta.mbti.model.Comment;
import com.sparta.mbti.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByUsername(String username);
    Optional<User> findByCommentId(Long commentId);
}