package com.sparta.mbti.repository;

import com.sparta.mbti.model.Image;
import com.sparta.mbti.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPost(Post post);
}
