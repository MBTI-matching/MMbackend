package com.sparta.mbti.controller;


import com.sparta.mbti.dto.request.UserRequestDto;
import com.sparta.mbti.dto.response.MbtiResponseDto;
import com.sparta.mbti.dto.response.PostResponseDto;
import com.sparta.mbti.dto.response.UserResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public UserResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        // 카카오 서버로부터 받은 인가 코드, JWT 토큰
        return userService.kakaoLogin(code, response);
    }

    // 내정보 입력 / 수정
    @PutMapping("/api/profile")
    public UserResponseDto updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestPart(value = "data") UserRequestDto userRequestDto,
                                         @RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile
    ) throws IOException {
        // 추가 정보 입력
        return userService.updateProfile(userDetails.getUser(), userRequestDto, multipartFile);
    }

    // 회원 탈퇴
    @DeleteMapping("/api/profile")
    public void deleteProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteProfile(userDetails.getUser());
    }

    // 내가 쓴 글 조회
    @GetMapping("/api/profile/mywrite")
    public List<PostResponseDto> getMyPosts(@RequestParam int page,
                                            @RequestParam int size,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userService.getMyposts(pageable, userDetails.getUser());
    }

    // 내 MBTI 세부 정보 조회
    @GetMapping("/api/profile/mbti")
    public MbtiResponseDto viewProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.viewProfile(userDetails.getUser());
    }

}

