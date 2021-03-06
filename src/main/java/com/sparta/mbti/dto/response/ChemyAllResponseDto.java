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
public class ChemyAllResponseDto {
    private String location;                // 위치
    private String locDetail;               // 상세위치
    private int userCount;                  // 케미 사용자 수
    List<ChemyUserResponseDto> userList;        // 케미 사용자 리스트
}
