package com.sparta.mbti.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoRequestDto {
    private String nickname;
    private String profileImage;
    private String intro;
    private String location; // 여기도 "우편번호(주소)찾기" 기능을 넣어야 하는 게 아닌가 싶다. 임시로 String으로 가나 수정해야 할 듯
    private List<UserInterestRequestDto> interestList;
    private String mbti;

//    @Builder
//    public UserInfoRequestDto(String nickname, String profileImage, String intro, String location, List<UserInterestRequestDto> interestList, String mbti) {
//        this.nickname = nickname;
//        this.profileImage = profileImage;
//        this.intro = intro;
//        this.location = location;
//        this.interestList = interestList;
//        this.mbti = mbti;
//    }

}
