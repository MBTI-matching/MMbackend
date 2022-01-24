package com.sparta.mbti.controller;

import com.sparta.mbti.dto.response.ChemyUserResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.ChemyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChemyController {
    private final ChemyService chemyService;

    // 자동 매칭
    @GetMapping("/api/chemy/auto")
    public ChemyUserResponseDto chemyAuto(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chemyService.chemyAuto(userDetails.getUser());
    }

    // 케미 사용자 선택
    @GetMapping("/api/chemy/{userId}")
    public ChemyUserResponseDto chemyUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chemyService.chemyUser(userDetails.getUser(), userId);
    }

    // 케미 사용자 상세 정보 보기 - 열람자와의 궁합 고려
    @GetMapping("/api/chemy/affinity/{userId}")
    public ChemyUserResponseDto affinityUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        return chemyService.affinityUser(userDetails.getUser(), userId);
    }
}
