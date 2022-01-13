package com.sparta.mbti.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponseDto {
    private Long matchingId;
    private Long hostId;
    private Long guestId;
}
