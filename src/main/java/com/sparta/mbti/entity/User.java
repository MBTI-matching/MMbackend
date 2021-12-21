package com.sparta.mbti.entity;

import com.sparta.mbti.dto.UserInfoRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private String intro;

    @Column
    private String location;

    @Column
    private String latitude;

    @Column
    private String longitude;

    @Column
    private String gender;

    @Column
    private String ageRange;

    @Column
    private String address;

    @Column
    private String mbti;

    @OneToMany
    @JoinColumn
    private List<Interest> interestList = new ArrayList<>();
    // 순환참조 방지 방법?

    public void updateProfile(UserInfoRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.intro = requestDto.getIntro();
        this.profileImage = requestDto.getProfileImage();
        this.location = requestDto.getLocation();
        this.mbti = requestDto.getMbti();
        this.interestList = requestDto.getInterestList();

    }
}
