package com.sparta.mbti.controller;

import com.sparta.mbti.dto.MatchingResponseDto;
import com.sparta.mbti.dto.response.MatchResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @DeleteMapping("/matching/receive/{hostId}")
    public String receiveMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long hostId, @RequestParam(value = "accept") boolean accept) {
        return matchingService.receiveMatching(userDetails.getUser(), hostId, accept);
    }

    // 신청한 내역 조회
    @GetMapping("/matching/send")
    public List<MatchResponseDto> sentMatching(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return matchingService.sentMatching(userDetails.getUser());
    }

    // 신청받은 내역 조회
    @GetMapping("/matching/receive")
    public List<MatchResponseDto> invitedMatching(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return matchingService.invitedMatching(userDetails.getUser());
    }

    // 신청 취소
    @DeleteMapping("/matching/{guestId}")
    public String deleteMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long guestId) {
        return matchingService.deleteMatching(userDetails.getUser(), guestId);
    }
}
