package com.sparta.mbti.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String username;            // 유저 아이디
    private String nickname;            // 닉네임
    private String profileImage;        // 프로필
    private String gender;              // 성별
    private String ageRange;            // 연령대
    private String intro;               // 소개글
    private String location;            // 위치 (서울 특별시 구)
    private String longitude;           // 경도
    private String latitude;            // 위도
    private String mbti;                // mbti
    List<String> interestList; // 관심사 리스트
    private boolean signStatus;         // 가입된 상태 (가입: true, 미가입: false)
}
