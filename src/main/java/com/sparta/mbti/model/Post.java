package com.sparta.mbti.model;

import com.sparta.mbti.dto.PostUndoDto;
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
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    public void update(PostUndoDto postUndoDto) {
        this.title = postUndoDto.getTitle();
        this.content = postUndoDto.getContent();
    }

//    @Column
//    private String interest;
//
//    @Column
//    private String mbti;
}
