package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private String content;     // 게시글 내용
    private String tag;         // 게시글 관심사 태그
    private List<ImageRequestDto> imageList;    // 이미지 리스트
}
