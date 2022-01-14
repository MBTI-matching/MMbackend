package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponseDto {
    private Long matchingId;
    private Long hostId;
    private Long guestId;
    private String guestImg;
    private String guestNick;
    private String guestMbti;
}