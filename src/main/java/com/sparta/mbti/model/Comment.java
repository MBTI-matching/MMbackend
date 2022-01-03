package com.sparta.mbti.model;

import com.sparta.mbti.dto.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;                         // 테이블 기본키

    @Column(columnDefinition = "LONGTEXT")
    private String comment;                 // 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;                      // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;                      // 게시글

    public void update(CommentRequestDto commentRequestDto) {

        this.comment = commentRequestDto.getComment();
    }
}