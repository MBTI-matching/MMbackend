package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentCreateDto;
import com.sparta.mbti.model.Comment;
import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.CommentRepository;
import com.sparta.mbti.repository.PostRepository;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional // 댓글 생성
    public void createComments(Long postId, CommentCreateDto requestDto, User user) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            throw new NullPointerException("해당 게시글이 존재하지 않습니다.");

        Comment comment = new Comment(requestDto.getComment(), post, user);
        commentRepository.save(comment);
    }

    @Transactional // 댓글 수정
    public void updateComment(Long commentId, CommentCreateDto requestDto, UserDetailsImpl userDetails) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        User writer = userRepository.findByCommentId(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글의 작성자가 존재하지 않습니다.")
        );

        if (!writer.equals(userDetails.getUser())) {
            throw new IllegalArgumentException("댓글은 작성자만 수정할 수 있습니다.");
        } else if (writer.equals(userDetails.getUser())) {
            comment.updateComment(requestDto);
        }
    }

    @Transactional // 댓글 삭제
    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {

        User writer = userRepository.findByCommentId(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글의 작성자가 존재하지 않습니다.")
        );

        if (!writer.equals(userDetails.getUser())) {
            throw new IllegalArgumentException("댓글은 작성자만 지울 수 있습니다.");
        } else if (writer.equals(userDetails.getUser())) {
            commentRepository.deleteById(commentId);
        }
    }
}
