package com.sparta.mbti.controller;

import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    //매칭 신청
    @PostMapping("/matching/{guestId}")
    public String requestMatching(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long guestId){
        return matchingService.requestMatching(userDetails.getUser(), guestId);
    }
}
