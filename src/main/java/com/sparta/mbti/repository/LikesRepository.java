package com.sparta.mbti.repository;

import com.sparta.mbti.model.Likes;
import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    List<Likes> findAllByPost(Post post);
}
