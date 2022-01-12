package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentResopnseDto;
import com.sparta.mbti.dto.ImageResponseDto;
import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.*;
import com.sparta.mbti.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final InterestRepository interestRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final S3Uploader s3Uploader;

    private final String imageDirName = "post";   // S3 폴더 경로

    // 게시글 작성
    @Transactional
    public void createPost(User user,
                           PostRequestDto postRequestDto,
                           List<MultipartFile> multipartFile
    ) throws IOException {
        // 요청한 정보로 게시글 객체 생성
        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .tag(postRequestDto.getTag())
                .user(user)
                .build();

        // DB 저장
        postRepository.save(post);

        // 이미지 리스트
        List<Image> imageList = new ArrayList<>();
        for (int i = 0; i < multipartFile.size(); i++) {
            String imgUrl = "";

            // 이미지 첨부 있으면 URL 에 S3에 업로드된 파일 url 저장
            if (multipartFile.get(i).getSize() != 0) {
                imgUrl = s3Uploader.upload(multipartFile.get(i), imageDirName);
            }
            imageList.add(Image.builder()
                            .imageLink(imgUrl)
                            .post(post)
                            .build());
        }

        // DB 저장
        imageRepository.saveAll(imageList);
    }

    // 게시글 상세 조회
    @Transactional
    public PostResponseDto detailPost(Long postId, User user) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        // 게시글 좋아요 수
        int likesCount = likesRepository.findAllByPost(post).size();
        // 게시글 좋아요 여부
        boolean likeStatus = likesRepository.existsByUserAndPost(user, post);
        // 게시글 이미지 리스트
        List<Image> imageList = imageRepository.findAllByPost(post);
        // 반환할 이미지 리스트
        List<ImageResponseDto> images = new ArrayList<>();
        for (Image oneImage : imageList) {
            images.add(ImageResponseDto.builder()
                            .imageId(oneImage.getId())
                            .imageLink(oneImage.getImageLink())
                            .build());
        }
        // 게시글에 달린 댓글 리스트 (작성일자 오름차순)
        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtAsc(post);
        // 반환할 댓글 리스트
        List<CommentResopnseDto> comments = new ArrayList<>();
        for (Comment oneComment : commentList) {
            comments.add(CommentResopnseDto.builder()
                            .commentId(oneComment.getId())
                            .nickname(oneComment.getUser().getNickname())
                            .image(oneComment.getUser().getProfileImage())
                            .mbti(oneComment.getUser().getMbti().getMbti())
                            .comment(oneComment.getComment())
                            .createdAt(oneComment.getCreatedAt())
                            .build());
        }

        // 반환할 상세 게시글
        return PostResponseDto.builder()
                .postId(post.getId())
                .nickname(post.getUser().getNickname())
                .profileImage(post.getUser().getProfileImage())
                .location(post.getUser().getLocation().getLocation())
                .mbti(post.getUser().getMbti().getMbti())
                .content(post.getContent())
                .tag(post.getTag())
                .likesCount(likesCount)
                .likeStatus(likeStatus)
                .imageList(images)
                .commentList(comments)
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, User user, PostRequestDto postRequestDto) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        if (!user.getId().equals(post.getUser().getId())) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 수정 가능합니다.");
        }

        // 해당 게시글 객체 정보 업데이트
        post.update(postRequestDto);

        // DB 저장
        postRepository.save(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, User user) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        if (!user.getId().equals(post.getUser().getId())) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 삭제 가능합니다.");
        }

        // DB 삭제
        postRepository.deleteById(postId);
    }

    // 게시글 좋아요
    @Transactional
    public void likesOnOff(Long postId, User user) {
        // 게시글 조회
        Post findPost = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        // 해당 사용자가 게시글에 좋아요 여부 조회
        Likes findLikes = likesRepository.findByUserAndPost(user, findPost).orElse(null);

        // 게시글에 좋아요 언했으면 추가, 했으면 삭제
        if (findLikes == null) {
            likesRepository.save(Likes.builder()
                    .user(user)
                    .post(findPost)
                    .build());
        } else {
            likesRepository.delete(findLikes);
        }
    }

    // 관심사별 게시글 목록 불러오기
    @Transactional
    public List<PostResponseDto> getIntPosts(Long interestId, Pageable pageable, User user) {

        Interest interest = interestRepository.findById(interestId).orElseThrow(
                () -> new NullPointerException("해당 관심사는 다루지 않습니다.")
        );

        // 태그(관심사명), page, size, 내림차순으로 페이징한 게시글 리스트
        List<Post> postList = postRepository.findAllByTagOrderByCreatedAtDesc(pageable, interest.getInterest()).getContent();

        // 반환할 게시글 리스트 설정
        List<PostResponseDto> posts = new ArrayList<>();
        for (Post onePost : postList) {

            // 게시글 좋아요 수
            int likesCount = (int)likesRepository.findAllByPost(onePost).size();
            // 게시글 좋아요 여부
            boolean likeStatus = likesRepository.existsByUserAndPost(user, onePost);
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
                        .image(oneComment.getUser().getProfileImage())
                        .mbti(oneComment.getUser().getMbti().getMbti())
                        .comment(oneComment.getComment())
                        .createdAt(oneComment.getCreatedAt())
                        .build());
            }

            posts.add(PostResponseDto.builder()
                    .postId(onePost.getId())
                    .nickname(onePost.getUser().getNickname())
                    .profileImage(onePost.getUser().getProfileImage())
                    .location(onePost.getUser().getLocation().getLocation())
                    .mbti(onePost.getUser().getMbti().getMbti())
                    .content(onePost.getContent())
                    .tag(onePost.getTag())
                    .likesCount(likesCount)
                    .likeStatus(likeStatus)
                    .imageList(images)
                    .commentList(comments)
                    .createdAt(onePost.getCreatedAt())
                    .build());
        }
        return posts;
    }
}
