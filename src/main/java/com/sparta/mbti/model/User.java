package com.sparta.mbti.model;

import com.sparta.mbti.dto.request.UserRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    public enum Role {
        ROLE_BOT, ROLE_ADMIN, ROLE_USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;                            // 테이블 기본키

    @Column(nullable = false, unique = true)
    private Long kakaoId;                       // 카카오 ID (카카오 기본키)

    @Column(nullable = false, unique = true)
    private String username;                    // 카카오 아이디 (이메일)

    @Column(nullable = false)
    private String password;                    // 카카오 비밀번호

    @Column(nullable = false)
    private String nickname;                    // 닉네임

    @Column
    private String profileImage;                 // 프로필 이미지

    @Column
    private String gender;                      // 카카오 성별

    @Column
    private String ageRange;                    // 카카오 연령대

    @Column(columnDefinition = "TEXT")
    private String intro;                       // 소개글

    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;                  // 위치 (특별시, 도)

    @ManyToOne
    @JoinColumn(name = "LOC_DETAIL_ID")
    private LocDetail locDetail;                // 구

    @Column
    private String locFull;                     // 전체 위치

    @Column
    private String longitude;

    @Column
    private String latitude;

    @ManyToOne
    @JoinColumn(name = "MBTI_ID")
    private Mbti mbti;                          // mbti

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)    // 사용자 삭제 => 해당 사용자관심사리스트 모두 삭제
    private final List<UserInterest> userInterestList = new ArrayList<>();    // 관심사 리스트

    @Column
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column
    private boolean status;                     // 카카오 추가정보 입력상태 체크

    @OneToMany(fetch = FetchType.EAGER)
    private final List<Matching> matchingList = new ArrayList<>();

    @Column
    private Point point;

    // 추가 입력 정보
    // DTO 모델에 안쓰기로 바꾸기
    public void update(UserRequestDto userRequestDto, String imgUrl, Location location, LocDetail locDetail, Mbti mbti, Point point, boolean status) {
        this.nickname = userRequestDto.getNickname();
        this.gender = userRequestDto.getGender();
        this.ageRange = userRequestDto.getAgeRange();
        this.intro = userRequestDto.getIntro();
        this.longitude = userRequestDto.getLongitude();
        this.latitude = userRequestDto.getLatitude();
        this.locFull = userRequestDto.getLocFull();
        this.profileImage = imgUrl;
        this.location = location;
        this.locDetail = locDetail;
        this.mbti = mbti;
        this.role = Role.ROLE_USER;
        this.point = point;
        this.status = status;
    }

}
