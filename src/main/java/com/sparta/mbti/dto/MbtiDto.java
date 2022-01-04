package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MbtiDto {
    private String name;
    private String firstTitle;
    private String firstContent;
    private String secondTitle;
    private String secondContent;
}
