package com.sparta.mbti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data // @Getter & @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
    private String comment;
}
