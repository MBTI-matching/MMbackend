package com.sparta.mbti.model;

import com.sparta.mbti.dto.KakaoUserInfoDto;
import com.sparta.mbti.dto.UserRequestDto;
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

    @Column
    private String interest;
    // JoinColumn은 아니지만 정말로 "복수의" 관심사를 나타내는 데 String으로 괜찮은가? String[]이나 List나 이런 걸로 해야 하는 거 아닌가?

    // 카카오 정보
    public void updateKakao(KakaoUserInfoDto kakaoUserInfo) {
        this.nickname = kakaoUserInfo.getNickname();
        this.profileImage = kakaoUserInfo.getProfileImage();
        this.gender = kakaoUserInfo.getGender();
        this.ageRange = kakaoUserInfo.getAgeRange();
    }

    // 추가 입력 정보
    public void update(UserRequestDto userRequestDto) {
        this.nickname = userRequestDto.getNickname();
        this.profileImage = userRequestDto.getProfileImage();
        this.intro = userRequestDto.getIntro();
        this.location = userRequestDto.getLocation();
        this.interest = userRequestDto.getInterest();
        this.mbti = userRequestDto.getMbti();
    }
}
