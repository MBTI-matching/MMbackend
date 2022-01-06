package com.sparta.mbti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;            // 게시글 ID
    private String nickname;        // 닉네임
    private String profileImage;    // 프로필 이미지
    private String location;        // 위치
    private String mbti;            // mbti
    private String content;         // 게시글 내용
    private String tag;             // 게시글 태그
    private int likesCount;         // 게시글 좋아요 수
    private boolean likeStatus;     // 게시글 좋아요 여부
    private List<ImageResponseDto> imageList;    // 이미지 리스트
    private List<CommentResopnseDto> commentList;    // 댓글 리스트

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Seoul")
    private LocalDateTime createdAt; // 생성날짜
}
