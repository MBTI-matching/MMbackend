package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDto {
    private Long imageId;           // 이미지 ID
    private String imageLink;       // 게시글 이미지 링크
}
