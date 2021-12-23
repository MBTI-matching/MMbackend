package com.sparta.mbti.controller;

import com.sparta.mbti.dto.CommentCreateDto;
import com.sparta.mbti.dto.CommentDto;
import com.sparta.mbti.model.User;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/comment/{postId}")
    public void createComments(@PathVariable Long postId, @RequestBody CommentCreateDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        commentService.createComments(postId, requestDto, user);
    }

    @PutMapping("/api/comment/{postId}/{commentId}")
    public void updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentCreateDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.updateComment(commentId, requestDto, userDetails);
    }


    @DeleteMapping("/api/comment/{postId}/{commentId}")
    public void deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long commentId, @PathVariable Long postId) {
        commentService.deleteComment(userDetails, commentId);
    }


}
