package com.sparta.mbti.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class ProfileController {

    private final Environment env;

    public ProfileController(Environment env) {
        this.env = env;
    }

    @GetMapping("/profile")
    public String getProfile() {
        System.out.println(Arrays.asList(env.getActiveProfiles())
                .stream()
                .findFirst()
                .orElse(""));
        System.out.println(Arrays.asList(env.getDefaultProfiles())
                .stream()
                .findFirst()
                .orElse(""));
        return Arrays.stream(env.getActiveProfiles())
                .findFirst()
                .orElse("");
    }

    @GetMapping("/profile/default")
    public String getDefault() {
        return Arrays.stream(env.getDefaultProfiles())
                .findFirst()
                .orElse("");
    }
}
