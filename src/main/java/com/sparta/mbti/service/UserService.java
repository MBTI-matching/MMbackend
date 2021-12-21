package com.sparta.mbti.service;

import com.sparta.mbti.dto.UserInfoRequestDto;
import com.sparta.mbti.dto.UserInterestRequestDto;
import com.sparta.mbti.entity.Interest;
import com.sparta.mbti.entity.User;
import com.sparta.mbti.repository.InterestRepository;
import com.sparta.mbti.repository.MbtiRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        //List<UserInterestRequestDto> interestRequestDtoList = new ArrayList<>(requestDto.getInterestList());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("해당 유저는 존재하지 않습니다.")
        );

        Interest interest = interestRepository.findByUserId(userId).orElseThrow(
                () -> new NullPointerException("해당 유저의 관심사가 없습니다.")
        );

        //List<UserInterestRequestDto> infoList = new ArrayList<>();

        for (UserInterestRequestDto interestDto : requestDto.getInterestList()) {
            if (interestDto == null) {
                throw new NullPointerException("관심사를 입력해주십시오.");
            }
//            else {
//                infoList.add(new UserInterestRequestDto(interestDto.getInterest()));
//            }
            interest.updateProfile(interestDto);
        }

        user.updateProfile(requestDto);
    }
}
