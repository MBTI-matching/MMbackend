package com.sparta.mbti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResopnseDto {
    private Long commentId;             // 댓글 ID
    private String nickname;            // 닉네임
    private String mbti;                // mbti
    private String comment;             // 댓글

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Seoul")
    private LocalDateTime createdAt;   // 생성날짜
}
