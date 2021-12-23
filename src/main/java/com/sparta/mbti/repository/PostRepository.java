package com.sparta.mbti.repository;

import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByUserAndId(User user, Long postId);

    Optional<Post> findById(Long postId);
}
