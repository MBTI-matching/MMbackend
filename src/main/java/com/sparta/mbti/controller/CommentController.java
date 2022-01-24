package com.sparta.mbti.controller;


import com.sparta.mbti.dto.request.CommentRequestDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/api/comment/{postId}")
    public void createComment(@PathVariable Long postId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              @RequestBody CommentRequestDto commentRequestDto) {
        commentService.createComment(postId, userDetails.getUser(), commentRequestDto);
    }

    // 댓글 수정
    // {postId} 실제로 이용하지 않고 있음
    @PutMapping("/api/comment/{postId}/{commentId}")
    public void updateComment(@PathVariable Long commentId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              @RequestBody CommentRequestDto commentRequestDto) {
        commentService.updateComment(commentId, userDetails.getUser(), commentRequestDto);
    }

    // 댓글 삭제
    // {postId} 실제로 이용하지 않고 있음
    @DeleteMapping("api/comment/{postId}/{commentId}")
    public void deleteComment(@PathVariable Long commentId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser());
    }
}
