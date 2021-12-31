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
public class ChemyAllResponseDto {
    private String location;                // 위치
    private String longitude;               // 경도
    private String latitude;                // 위도
    private int userCount;                  // 케미 사용자 수
    List<ChemyUserListDto> userList;        // 케미 사용자 리스트
}
