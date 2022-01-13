package com.sparta.mbti.controller;

import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    //매칭 신청
    @PostMapping("/matching/{guestId}")
    public String requestMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long guestId) {
        return matchingService.requestMatching(userDetails.getUser(), guestId);
    }

    // 매칭 신청 수락/거절 뒤 요청 목록에서 삭제: userDetails.getUser()를 guest로 설정
    @GetMapping("/matching/receive/{hostId}")
    public String deleteMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long hostId, @RequestParam(value = "accept") boolean accept) {
        return matchingService.receiveMatching(userDetails.getUser(), hostId, accept);
    }
}
