package com.sparta.mbti.repository;

import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findAllByUser(User user, Pageable pageable);

    Page<Post> findAllByTagOrderByCreatedAtDesc(Pageable pageable, String tag);

    List<Post> findAllByUser(User findUser);
}
