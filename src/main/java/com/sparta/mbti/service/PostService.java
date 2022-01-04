package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentResopnseDto;
import com.sparta.mbti.dto.ImageResponseDto;
import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.CommentRepository;
import com.sparta.mbti.repository.ImageRepository;
import com.sparta.mbti.repository.LikesRepository;
import com.sparta.mbti.repository.PostRepository;
import com.sparta.mbti.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
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
    public PostResponseDto detailPost(Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        // 게시글 좋아요 수
        int likesCount = (int)likesRepository.findAllByPost(post).size();
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
                            .mbti(oneComment.getUser().getMbti().getMbti())
                            .comment(oneComment.getComment())
                            .createdAt(oneComment.getCreatedAt())
                            .build());
        }

        // 반환할 상세 게시글
        return PostResponseDto.builder()
                .postId(post.getId())
                .nickname(post.getUser().getNickname())
                .mbti(post.getUser().getMbti().getMbti())
                .content(post.getContent())
                .tag(post.getTag())
                .likesCount(likesCount)
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

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}
