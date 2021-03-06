package com.sparta.mbti.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String locDetail;           // 상세위치
    private String mbti;                // MBTI
    private String username;            // 유저 이메일
    private String affinity;            // 사이
    private String detail;              // 친해지는법
    List<String> interestList; // 관심사 리스트 // dto 안 builder -> stream
}
