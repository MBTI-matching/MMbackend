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
public class UserRequestDto {
    private String nickname;            // 닉네임
    private String profileImage;        // 프로필
    private String gender;              // 성별
    private String ageRange;            // 연령대
    private String intro;               // 소개글
    private String location;            // 위치 (서울 특별시 구)
    private String mbti;                // mbti
    List<InterestListDto> interestList; // 관심사 리스트
}
