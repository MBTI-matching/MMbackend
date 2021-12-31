package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChemyUserListDto {
    private Long userId;                // 사용자 ID
    private String nickname;            // 닉네임
    private String profileImage;        // 프로필 이미지
    private String intro;               // 소개글
    private String location;            // 위치
    private String mbti;                // MBTI
}
