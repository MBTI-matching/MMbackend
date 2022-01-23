package com.sparta.mbti.service;

import com.sparta.mbti.dto.response.ChemyUserResponseDto;
import com.sparta.mbti.dto.response.UserAffinityResponseDto;
import com.sparta.mbti.model.Mbti;
import com.sparta.mbti.model.User;
import com.sparta.mbti.model.UserInterest;
import com.sparta.mbti.repository.MbtiRepository;
import com.sparta.mbti.repository.UserInterestRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ChemyService {
    private final MbtiRepository mbtiRepository;
    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;

    // 전국 매칭
    @Transactional
    public UserAffinityResponseDto chemyAuto(User user) {

        String bestMatch = user.getMbti().getBestMatch();
        List<Mbti> foundMbtiList = new ArrayList<>();

        List<Mbti> allList = mbtiRepository.findAll();
        for (Mbti one : allList) {
            if (bestMatch.contains(one.getMbti())) {
                foundMbtiList.add(one);
            }
        }
        // MBTI // ROLE  가장 이상적인 사용자 리스트 조회
        List<User> findUserList = userRepository.findAllByRoleAndMbtiIn(User.Role.ROLE_USER, foundMbtiList);

        // 궁합
        String affinity;

        // 사용자 리스트 수 범위만큼 랜덤 생성 (10 이면 0~9 랜덤 생성)
        Random generator = new Random();
        int size = 0;
        if (!findUserList.isEmpty()) {
            size = generator.nextInt(findUserList.size());

            // 랜덤 사용자 관심사 리스트 조회
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUserList.get(size));
            List<String> interestList = new ArrayList<>();
            for (UserInterest userInterest : userInterestList) {
                interestList.add(userInterest.getInterest().getInterest());
            }

            if (user.getMbti().getBestMatch().contains(findUserList.get(size).getMbti().getMbti())) {
                affinity = "소울메이트";
            } else if (user.getMbti().getGoodMatch().contains(findUserList.get(size).getMbti().getMbti())) {
                affinity = "좋은 사이";
            } else if (user.getMbti().getBadMatch().contains(findUserList.get(size).getMbti().getMbti())) {
                affinity = "어려운 사이";
            } else {
                affinity = "무난한 사이";
            }


            // 랜덤 사용자 반환
            return UserAffinityResponseDto.builder()
                    .username(findUserList.get(size).getUsername())
                    .userId(findUserList.get(size).getId())
                    .nickname(findUserList.get(size).getNickname())
                    .profileImage(findUserList.get(size).getProfileImage())
                    .gender(findUserList.get(size).getGender())
                    .ageRange(findUserList.get(size).getAgeRange())
                    .intro(findUserList.get(size).getIntro())
                    .location(findUserList.get(size).getLocation().getLocation())
                    .locDetail(findUserList.get(size).getLocDetail().getLocDetail())
                    .mbti(findUserList.get(size).getMbti().getMbti())
                    .affinity(affinity)
                    .interestList(interestList)
                    .build();
        } else {

            List<User> findLocUserList = userRepository.findAllByLocationAndLocDetailAndMbtiIn(user.getLocation(), user.getLocDetail(), foundMbtiList);
            // 사용자 리스트 수 범위만큼 랜덤 생성 (10 이면 0~9 랜덤 생성)
            Random votGenerator = new Random();
            int votSize = 0;
            if (findLocUserList.size() > 0) {
                votSize = votGenerator.nextInt(findLocUserList.size());

                // 랜덤 사용자 관심사 리스트 조회
                List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findLocUserList.get(votSize));
                List<String> interestList = new ArrayList<>();
                for (UserInterest userInterest : userInterestList) {
                    interestList.add(userInterest.getInterest().getInterest());
                }

                if (user.getMbti().getBestMatch().contains(findLocUserList.get(votSize).getMbti().getMbti())) {
                    affinity = "소울메이트";
                } else if (user.getMbti().getGoodMatch().contains(findLocUserList.get(votSize).getMbti().getMbti())) {
                    affinity = "좋은 사이";
                } else if (user.getMbti().getBadMatch().contains(findLocUserList.get(votSize).getMbti().getMbti())) {
                    affinity = "어려운 사이";
                } else {
                    affinity = "무난한 사이";
                }

                // 랜덤 사용자 반환
                return UserAffinityResponseDto.builder()
                        .username(findLocUserList.get(votSize).getUsername())
                        .userId(findLocUserList.get(votSize).getId())
                        .nickname(findLocUserList.get(votSize).getNickname())
                        .profileImage(findLocUserList.get(votSize).getProfileImage())
                        .gender(findLocUserList.get(votSize).getGender())
                        .ageRange(findLocUserList.get(votSize).getAgeRange())
                        .intro(findLocUserList.get(votSize).getIntro())
                        .location(findLocUserList.get(votSize).getLocation().getLocation())
                        .locDetail(findLocUserList.get(votSize).getLocDetail().getLocDetail())
                        .mbti(findLocUserList.get(votSize).getMbti().getMbti())
                        .affinity(affinity)
                        .interestList(interestList)
                        .build();
            }
        }
        return UserAffinityResponseDto.builder()
                .userId(-1L)
                .build();
    }

//    // 자동 매칭
//    @Transactional
//    public ChemyUserResponseDto chemyAuto(User user) {
//        // 사용자 가장 이상적 MBTI 조회
//        Mbti findMbti = mbtiRepository.findByMbtiFirst(user.getMbti().getMbti()).orElseThrow(
//                () -> new IllegalArgumentException("해당 MBTI가 존재하지 않습니다.")
//        );
//
//        // 위치 / 상세위치 / MBTI 와 가장 이상적인 사용자 리스트 조회
//        List<User> findUserList = userRepository.findAllByLocationAndLocDetailAndMbti(user.getLocation(), user.getLocDetail(), findMbti);
//        // 사용자 리스트 수 범위만큼 랜덤 생성 (10 이면 0~9 랜덤 생성)
//        Random generator = new Random();
//        int size = 0;
//        if (findUserList.size() > 0) {
//            size = generator.nextInt(findUserList.size());
//
//            // 랜덤 사용자 관심사 리스트 조회
//            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUserList.get(size));
//            List<String> interestList = new ArrayList<>();
//            for (UserInterest userInterest : userInterestList) {
//                interestList.add(userInterest.getInterest().getInterest());
//            }
//
//            // 랜덤 사용자 반환
//            return ChemyUserResponseDto.builder()
//                    .username(findUserList.get(size).getUsername())
//                    .userId(findUserList.get(size).getId())
//                    .nickname(findUserList.get(size).getNickname())
//                    .profileImage(findUserList.get(size).getProfileImage())
//                    .gender(findUserList.get(size).getGender())
//                    .ageRange(findUserList.get(size).getAgeRange())
//                    .intro(findUserList.get(size).getIntro())
//                    .location(findUserList.get(size).getLocation().getLocation())
//                    .locDetail(findUserList.get(size).getLocDetail().getLocDetail())
//                    .mbti(findUserList.get(size).getMbti().getMbti())
//                    .interestList(interestList)
//                    .build();
//        }
//        return ChemyUserResponseDto.builder()
//                .userId(-1L)
//                .build();
//    }

    // 케미 사용자 선택
    @Transactional
    public ChemyUserResponseDto chemyUser(Long userId) {
        // 사용자 조회
        User findUser = userRepository.getById(userId);

        // 관심사 리스트 조회
        List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
        List<String> interestList = new ArrayList<>();
        for (UserInterest userInterest : userInterestList) {
            interestList.add(userInterest.getInterest().getInterest());
        }

        // 반환
        return ChemyUserResponseDto.builder()
                .username(findUser.getUsername())
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .profileImage(findUser.getProfileImage())
                .gender(findUser.getGender())
                .ageRange(findUser.getAgeRange())
                .intro(findUser.getIntro())
                .location(findUser.getLocation().getLocation())
                .locDetail(findUser.getLocDetail().getLocDetail())
                .mbti(findUser.getMbti().getMbti())
                .interestList(interestList)
                .build();
    }

    public UserAffinityResponseDto affinityUser(User user, Long userId) {

        // 사용자 조회
        User findUser = userRepository.getById(userId);

        // 관심사 리스트 조회
        List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
        List<String> interestList = new ArrayList<>();
        for (UserInterest userInterest : userInterestList) {
            interestList.add(userInterest.getInterest().getInterest());
        }

        // 궁합
        String affinity;

        if (user.getMbti().getBestMatch().contains(findUser.getMbti().getMbti())) {
            affinity = "소울메이트";
        } else if (user.getMbti().getGoodMatch().contains(findUser.getMbti().getMbti())) {
            affinity = "좋은 사이";
        } else if (user.getMbti().getBadMatch().contains(findUser.getMbti().getMbti())) {
            affinity = "어려운 사이";
        } else {
            affinity = "무난한 사이";
        }

        return UserAffinityResponseDto.builder()
                .username(findUser.getUsername())
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .profileImage(findUser.getProfileImage())
                .gender(findUser.getGender())
                .ageRange(findUser.getAgeRange())
                .intro(findUser.getIntro())
                .location(findUser.getLocation().getLocation())
                .locDetail(findUser.getLocDetail().getLocDetail())
                .mbti(findUser.getMbti().getMbti())
                .interestList(interestList)
                .affinity(affinity)
                .build();
    }
}
