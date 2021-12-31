package com.sparta.mbti.repository;

import com.sparta.mbti.model.Comment;
import com.sparta.mbti.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderByCreatedAtAsc(Post post);
}
