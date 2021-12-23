package com.sparta.mbti.controller;

import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.dto.PostUndoDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/api/post")
    public void createPosts(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.createPost(postRequestDto, userDetails.getUser());
    }

    // 상세 게시글 보기
    @GetMapping("/api/post/{postId}")
    public ResponseEntity<PostResponseDto> showDetail(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResponseDto postResponseDto = postService.showDetail(postId, userDetails.getUser());
        return ResponseEntity.ok()
                .body(postResponseDto);
    }

    //게시글 수정
    @PutMapping("/api/post/{postId}")
    public void updatePost(@PathVariable Long postId, @RequestBody PostUndoDto postUndoDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.updatePost(postId, postUndoDto, userDetails.getUser());
    }

    @DeleteMapping("/api/post/{postId}")
    public void deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
    }

}
