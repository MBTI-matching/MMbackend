package com.sparta.mbti.repository;

import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findAllByUserOrderByCreatedAtDesc(Pageable pageable, User user);
}
