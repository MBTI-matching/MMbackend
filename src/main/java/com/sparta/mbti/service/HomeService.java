package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentResopnseDto;
import com.sparta.mbti.dto.ImageResponseDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.model.Comment;
import com.sparta.mbti.model.Image;
import com.sparta.mbti.model.Post;
import com.sparta.mbti.repository.CommentRepository;
import com.sparta.mbti.repository.ImageRepository;
import com.sparta.mbti.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HomeService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;

    public List<PostResponseDto> getAllposts(Pageable pageable) {
        // page, size, 내림차순으로 페이징한 게시글 리스트
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        // 반환할 게시글 리스트
        List<PostResponseDto> posts = new ArrayList<>();

        for (Post onePost : postList) {
            // 게시글 이미지 리스트
            List<Image> imageList = imageRepository.findAllByPost(onePost);
            // 반환할 이미지 리스트
            List<ImageResponseDto> images = new ArrayList<>();
            for (Image oneImage : imageList) {
                images.add(ImageResponseDto.builder()
                        .imageId(oneImage.getId())
                        .imageLink(oneImage.getImageLink())
                        .build());
            }

            // 게시글에 달린 댓글 리스트 (작성일자 오름차순)
            List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtAsc(onePost);
            // 반환할 댓글 리스트
            List<CommentResopnseDto> comments = new ArrayList<>();
            for (Comment oneComment : commentList) {
                comments.add(CommentResopnseDto.builder()
                                            .commentId(oneComment.getId())
                                            .nickname(oneComment.getUser().getNickname())
                                            .mbti(oneComment.getUser().getMbti().getMbti())
                                            .comment(oneComment.getComment())
                                            .createdAt(oneComment.getCreatedAt())
                                            .build());
            }
            // 전체 게시글 리스트
            posts.add(PostResponseDto.builder()
                                .postId(onePost.getId())
                                .nickname(onePost.getUser().getNickname())
                                .mbti(onePost.getUser().getMbti().getMbti())
                                .content(onePost.getContent())
                                .tag(onePost.getTag())
                                .imageList(images)
                                .commentList(comments)
                                .createdAt(onePost.getCreatedAt())
                                .build());
        }
        return posts;
    }
}
