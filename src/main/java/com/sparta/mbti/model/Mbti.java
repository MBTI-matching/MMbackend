package com.sparta.mbti.model;

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
public class Mbti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MBTI_ID")
    private Long id;                            // 테이블 기본키

    @Column
    private String mbti;                        // mbti

    @Column
    private String mbtiFirst;                   // mbti 이상적 1단계

    @Column
    private String mbtiSecond;                  // mbti 이상적 2단계

    @Column
    private String mbtiThird;                   // mbti 이상적 3단계

    @Column
    private String mbtiForth;                   // mbti 이상적 4단계

    @Column
    private String bestMatch;                   // 소울메이트

    @Column
    private String goodMatch;                   // 좋은 사이

    @Column
    private String badMatch;                    // 안 맞는 사이

    @Column
    private String firstTitle;                  // 제목 #1

    @Column(columnDefinition = "TEXT")
    private String firstContent;                // 설명 #1

    @Column
    private String secondTitle;                 // 제목 #2

    @Column(columnDefinition = "TEXT")
    private String secondContent;               // 설명 #2
}
