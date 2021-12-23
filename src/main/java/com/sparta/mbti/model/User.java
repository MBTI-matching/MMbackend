package com.sparta.mbti.model;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    @Column
    private String gender;

    @Column
    private String ageRange;

    @Column(columnDefinition = "LONGTEXT")
    private String intro;

    @Column
    private String mbti;

    @Column
    private String location;

//    @Column
//    private String interest;
    // JoinColumn은 아니지만 정말로 "복수의" 관심사를 나타내는 데 String으로 괜찮은가? String[]이나 List나 이런 걸로 해야 하는 거 아닌가?


}
