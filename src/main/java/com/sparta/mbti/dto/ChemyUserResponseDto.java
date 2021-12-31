package com.sparta.mbti.dto;

import com.sparta.mbti.model.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChemyUserResponseDto {
    private Long userId;                // 사용자 ID
    private String nickname;            // 닉네임
    private String profileImage;        // 프로필 이미지
    private String gender;              // 성별
    private String ageRange;            // 연령대
    private String intro;               // 소개글
    private String location;            // 위치
    private String mbti;                // MBTI
    List<InterestListDto> interestList; // 관심사 리스트
}
