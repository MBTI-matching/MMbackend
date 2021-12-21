package com.sparta.mbti.service;

import com.sparta.mbti.dto.UserInfoRequestDto;
import com.sparta.mbti.entity.Interest;
import com.sparta.mbti.entity.Mbti;
import com.sparta.mbti.entity.User;
import com.sparta.mbti.repository.InterestRepository;
import com.sparta.mbti.repository.MbtiRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MbtiRepository mbtiRepository;
    private final InterestRepository interestRepository;

    @Transactional
    public void updateProfile(UserInfoRequestDto requestDto) {

        Long userId = 1L;
        // Long userId = user.getUserId();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("해당 유저가 없습니다.")
        );
        user.updateProfile(requestDto);
    }
}
