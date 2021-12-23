package com.sparta.mbti.model;

import com.sparta.mbti.dto.CommentCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "LONGTEXT")
    private String comment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Post post;

    @ManyToOne
    @JoinColumn
    private User user;

    public Comment(String comment, Post post, User user) {
        this.comment = comment;
        this.post = post;
        this.user = user;
    }

    public void updateComment(CommentCreateDto requestDto) {
        this.comment = requestDto.getComment();
    }
}
