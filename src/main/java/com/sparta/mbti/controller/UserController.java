package com.sparta.mbti.controller;

import com.sparta.mbti.dto.UserInfoRequestDto;
import com.sparta.mbti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/api/profile/edit")
    public void updateProfile(@RequestBody UserInfoRequestDto requestDto) {
        //@AuthenticationPrincipal UserDetailsImpl userDetails
        userService.updateProfile(requestDto);
    }
}
