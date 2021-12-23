package com.sparta.mbti.repository;

import com.sparta.mbti.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    //Optional<Comment> findById(Long commentId);

    void deleteAllByPostId(Long postId);

    Optional<Comment> findById(Long commentId);
}
