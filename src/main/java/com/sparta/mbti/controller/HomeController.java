package com.sparta.mbti.controller;

import com.sparta.mbti.dto.response.ChemyAllResponseDto;
import com.sparta.mbti.dto.response.PostResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class HomeController {
    private final HomeService homeService;

    // 전체 케미 리스트 (위치 / MBTI)
    @GetMapping("/api/chemy/list")
    public ChemyAllResponseDto chemyList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return homeService.chemyList(userDetails.getUser());
    }

    // 둘러보기
    @GetMapping("/api/chemy/guest")
    public ChemyAllResponseDto chemyGuest() {
        return homeService.chemyGuest();
    }

    // 지역 케미 리스트 (위치 / MBTI)
    @GetMapping("/api/chemy/list/{locationId}/{locDetailId}")
    public ChemyAllResponseDto locationList(@PathVariable Long locationId,
                                            @PathVariable Long locDetailId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return homeService.locationList(locationId, locDetailId, userDetails.getUser());
    }

    // 지역 & 관심사별 케미 리스트: (위치 / 관심사)
    @GetMapping("/api/chemy/{locationId}/{locDetailId}/{interestId}")
    public ChemyAllResponseDto interestList(@PathVariable Long locationId,
                                            @PathVariable Long locDetailId,
                                            @PathVariable Long interestId) {
        return homeService.interestList(locationId, locDetailId, interestId);
    }

    // 지역 & 관심사별 케미 리스트: (위치 / 관심사 / MBTI)
    @GetMapping("/api/chemy/list/{locationId}/{locDetailId}/{interestId}")
    public ChemyAllResponseDto chemyInterest(@PathVariable Long locationId,
                                             @PathVariable Long locDetailId,
                                             @PathVariable Long interestId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return homeService.chemyInterest(locationId, locDetailId, interestId, userDetails.getUser());
    }

    // 커뮤니티 (전체)
    @GetMapping("/api/post")
    public List<PostResponseDto> getAllPosts(@RequestParam int page,
                                             @RequestParam int size,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return homeService.getAllposts(pageable, userDetails.getUser());
    }
}
