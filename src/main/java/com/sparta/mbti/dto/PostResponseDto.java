package com.sparta.mbti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data // @Getter & @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private String interest;
    private String username;
    private String mbti;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    private String title;
    private String content;
    private List<CommentDto> commentList;
}
