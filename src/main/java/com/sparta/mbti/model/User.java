package com.sparta.mbti.model;

import com.sparta.mbti.dto.KakaoUserInfoDto;
import com.sparta.mbti.dto.UserRequestDto;
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
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;                            // 테이블 기본키

    @Column(nullable = false, unique = true)
    private Long kakaoId;                       // 카카오 ID (카카오 기본키)

    @Column(nullable = false, unique = true)
    private String username;                    // 카카오 아이디 (이메일)

    @Column(nullable = false)
    private String password;                    // 카카오 비밀번호

    @Column(nullable = false)
    private String nickname;                    // 카카오 닉네임

    @Column
    private String profileImage;                // 카카오 프로필

    @Column
    private String gender;                      // 카카오 성별

    @Column
    private String ageRange;                    // 카카오 연령대

    @Column
    private String intro;                       // 소개글

    @Column
    private String location;                    // 위치 (서울 특별시 구)

    @Column
    private String longitude;                   // 경도

    @Column
    private String latitude;                    // 위도

    @Column
    private String interest;                    // 관심사

    @Column
    private String mbti;                        // mbti

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
        this.longitude = userRequestDto.getLongitude();
        this.latitude = userRequestDto.getLatitude();
        this.interest = userRequestDto.getInterest();
        this.mbti = userRequestDto.getMbti();
    }
}
