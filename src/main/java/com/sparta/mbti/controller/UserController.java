package com.sparta.mbti.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mbti.dto.LoginInfo;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.dto.UserRequestDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public LoginInfo kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        // 카카오 서버로부터 받은 인가 코드, JWT 토큰
        userService.kakaoLogin(code, response);
        response.sendRedirect("http://localhost:8080/chat/room");

        return LoginInfo.builder().name(kakaoLogin(code, response).getName()).token(kakaoLogin(code, response).getToken()).build();
    }

    // 내정보 입력 / 수정
    @PutMapping("/api/profile")
    public void updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestDto userRequestDto) {
        // 추가 정보 입력
        userService.updateProfile(userDetails.getUser(), userRequestDto);
    }

    // 내가 쓴 글 조회
    @GetMapping("/api/profile/mywrite")
    public List<PostResponseDto> getMyPosts(@RequestParam int page,
                                            @RequestParam int size,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userService.getMyposts(pageable, userDetails.getUser());
    }
}

