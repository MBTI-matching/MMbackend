package com.sparta.mbti.controller;

import com.sparta.mbti.dto.MatchingResponseDto;
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
    public String requestMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long guestId){
        return matchingService.requestMatching(userDetails.getUser(), guestId);
    }

    //매칭 보낸 내역 확인
    @GetMapping("/matching/send")
    public List<MatchingResponseDto> sendMatching(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return matchingService.sendMatching(userDetails.getUser());
    }

    //매칭 받은 내역 확인
    @GetMapping("/matching/receive")
    public List<MatchingResponseDto> receiveMatching(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return matchingService.receiveMatching(userDetails.getUser());
    }

    //매칭 내역 삭제(매칭 거절, 매칭 취소)
   @DeleteMapping("/matching/{guestId}")
    public void deleteMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long guestId){
        matchingService.deleteMatching(userDetails.getUser(), guestId);
    }

}
