package com.sparta.mbti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data // @Getter & @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private String username;
    private String comment;
    private String mbti;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;
}
