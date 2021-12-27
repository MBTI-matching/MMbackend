package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentRequestDto;
import com.sparta.mbti.model.Comment;
import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.CommentRepository;
import com.sparta.mbti.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 댓글 작성
    @Transactional
    public void createComment(Long postId, User user, CommentRequestDto commentRequestDto) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        // 댓글 작성
        Comment comment = Comment.builder()
                            .comment(commentRequestDto.getComment())
                            .user(user)
                            .post(post)
                            .build();
        // DB 저장
        commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, User user, CommentRequestDto commentRequestDto) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IllegalArgumentException("해당 댓글의 작성자만 수정 가능합니다.");
        }

        // 해당 댓글 객체 정보 업데이트
        comment.update(commentRequestDto);

        // DB 저장
        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, User user) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IllegalArgumentException("해당 댓글의 작성자만 삭제 가능합니다.");
        }

        // 댓글 삭제
        commentRepository.deleteById(commentId);
    }
}
