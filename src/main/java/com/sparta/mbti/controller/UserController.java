package com.sparta.mbti.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mbti.dto.LoginInfoDto;
import com.sparta.mbti.dto.UserRequestDto;
import com.sparta.mbti.dto.UserResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.security.jwt.JwtTokenUtils;
import com.sparta.mbti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // 카카오 서버로부터 받은 인가 코드, JWT 토큰
        userService.kakaoLogin(code, response);
        return "redirect:/chat/room";
    }

    // 내정보 입력 / 수정
    @PutMapping("/api/profile")
    public void updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody  UserRequestDto userRequestDto) {
        // 추가 정보 입력
        userService.updateProfile(userDetails.getUser(), userRequestDto);
    }
    @GetMapping("/chat/user")
    @ResponseBody
    public LoginInfoDto getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String token = JWT.create()
                .withIssuer("sparta")
                .withClaim("USER_NAME", name)
                // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                .withClaim("EXPIRED_DATE", new Date(System.currentTimeMillis() + 86400000))
                .sign(Algorithm.HMAC256("jwtsecret!@#$%"));
        System.out.println(token + " usercontroller");
        return LoginInfoDto.builder().name(name).token(token).build();
    }

}