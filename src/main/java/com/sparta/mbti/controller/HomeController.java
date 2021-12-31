package com.sparta.mbti.controller;

import com.sparta.mbti.dto.ChemyAllResponseDto;
import com.sparta.mbti.dto.PostResponseDto;
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

    // 지역 케미 리스트 (위치 / MBTI)
    @GetMapping("/api/chemy/list/{locationId}")
    public ChemyAllResponseDto locationList(@PathVariable Long locationId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return homeService.locationList(locationId, userDetails.getUser());
    }

    // 커뮤니티 (전체)
    @GetMapping("/api/post")
    public List<PostResponseDto> getAllPosts(@RequestParam int page,
                                             @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return homeService.getAllposts(pageable);
    }
}
