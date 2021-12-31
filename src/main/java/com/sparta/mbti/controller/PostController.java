package com.sparta.mbti.controller;

import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping("/api/post")
    public void createPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestBody PostRequestDto postRequestDto) {
        postService.createPost(userDetails.getUser(), postRequestDto);
    }

    // 게시글 상세 조회
    @GetMapping("/api/post/{postId}")
    public PostResponseDto detailPost(@PathVariable Long postId) {
        return postService.detailPost(postId);
    }

    // 게시글 수정
    @PutMapping("/api/post/{postId}")
    public void updatePost(@PathVariable Long postId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestBody PostRequestDto postRequestDto) {
        postService.updatePost(postId, userDetails.getUser(), postRequestDto);
    }

    // 게시글 삭제
    @DeleteMapping("/api/post/{postId}")
    public void deletePost(@PathVariable Long postId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails.getUser());
    }

    // 게시글 좋아요
    @PostMapping("/api/post/likes/{postId}")
    public void likesOnOff(@PathVariable Long postId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.likesOnOff(postId, userDetails.getUser());
    }
}
