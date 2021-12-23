package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data // @Getter & @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private String interest; // 관심사
    private String mbti; // MBTI
    private String title; // 게시글 제목
    private String content; // 게시글 내용
}
