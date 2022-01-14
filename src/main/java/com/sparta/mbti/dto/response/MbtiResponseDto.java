package com.sparta.mbti.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MbtiResponseDto {
    private String name;
    private String firstTitle;
    private String firstContent;
    private String secondTitle;
    private String secondContent;
}
