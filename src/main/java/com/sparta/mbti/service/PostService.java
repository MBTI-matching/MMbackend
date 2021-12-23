package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentDto;
import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.dto.PostUndoDto;
import com.sparta.mbti.model.Comment;
import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.CommentRepository;
import com.sparta.mbti.repository.PostRepository;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.validator.UserInfoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createPost(PostRequestDto postRequestDto, User user) {

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .user(user)
                .build();

        postRepository.save(post);
    }

    @Transactional
    public PostResponseDto showDetail(Long postId, User user) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글 정보가 존재하지 않습니다.")
        );

        List<Comment> commentList = commentRepository.findAllByPostId(postId);

        List<CommentDto> comments = new ArrayList<>();
        for (Comment oneComment : commentList) {
            String username = oneComment.getUser().getUsername();
            String comment = oneComment.getComment();
            String mbti = oneComment.getUser().getMbti();
            LocalDateTime modifiedAt = oneComment.getModifiedAt();

            comments.add(new CommentDto(username, comment, mbti, modifiedAt));
        }

        return PostResponseDto.builder()
                .username(post.getUser().getUsername())
                .mbti(post.getUser().getMbti())
                .createdAt(post.getCreatedAt())
                .title(post.getTitle())
                .content(post.getContent())
                .commentList(comments)
                .build();
    }

    @Transactional
    public void updatePost(Long postId, PostUndoDto postUndoDto, User user) {
        Post post = postRepository.findByUserAndId(user, postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        post.update(postUndoDto);
    }

    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        User user = UserInfoValidator.userDetailsIsNull(userDetails);

        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            throw new NullPointerException("유효하지 않거나 이미 삭제된 글입니다.");
        }
        if (!user.getId().equals(post.get().getUser().getId())) {
            throw new IllegalArgumentException("당신의 게시글이 아닙니다.");
        }
        commentRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
    }
}
