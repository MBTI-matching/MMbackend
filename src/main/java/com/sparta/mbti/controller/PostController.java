package com.sparta.mbti.controller;

import com.sparta.mbti.dto.request.PostRequestDto;
import com.sparta.mbti.dto.response.PostResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping("/api/post")
    public void createPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestPart(value = "data") PostRequestDto postRequestDto,
                           @RequestPart(value = "multipartFile", required = false) List<MultipartFile> multipartFile
    ) throws IOException {
        postService.createPost(userDetails.getUser(), postRequestDto, multipartFile);
    }

    // 게시글 상세 조회
    @GetMapping("/api/post/{postId}")
    public PostResponseDto detailPost(@PathVariable Long postId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.detailPost(postId, userDetails.getUser());
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

    // 관심사별 게시글 목록 불러오기
    @GetMapping("/api/post/interest/{interestId}")
    public List<PostResponseDto> getIntPosts(@PathVariable Long interestId,
                                             @RequestParam int page,
                                             @RequestParam int size,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postService.getIntPosts(interestId, pageable, userDetails.getUser());
    }
}
