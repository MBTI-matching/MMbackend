package com.sparta.mbti.model;

import com.sparta.mbti.dto.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;                        // 테이블 기본키

    @Column(columnDefinition = "LONGTEXT")
    private String content;                 // 게시글 내용

    @Column
    private String tag;                     // 게시글 태그 (게시글 태그별로 분류)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;                      // 사용자

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private final List<Likes> likesList = new ArrayList<>();          // 게시글 삭제 => 해당 게시글 좋아요 모두 삭제

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private final List<Image> imageList = new ArrayList<>();          // 게시글 삭제 => 해당 게시글 이미지 모두 삭제

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)    // 게시글 삭제 => 해당 게시글 댓글 모두 삭제
    private final List<Comment> commentList = new ArrayList<>();

    public void update(PostRequestDto postRequestDto) {
        this.content = postRequestDto.getContent();
        this.tag = postRequestDto.getTag();
    }
}
