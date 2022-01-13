package com.sparta.mbti.service;

import com.sparta.mbti.model.Matching;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.MatchingRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MatchingRepository matchingRepository;

     /*매칭 신청
     1. 매칭 신청 대기 상태인지 확인
     2. 이미 매칭이 성사되어 채팅중인 상태인지 확인
     3. 매칭 신청 보내기*/
    public String requestMatching (User user, Long guestId){
        if(matchingRepository.existsByHostIdAndGuestId(user.getId(), guestId) || matchingRepository.existsByHostIdAndGuestId(guestId, user.getId())){
            return "신청 대기 상태입니다.";
        }

        if(chatRoomRepository.existsByHostIdAndGuestId(user.getId(), guestId) || chatRoomRepository.existsByHostIdAndGuestId(guestId, user.getId())){
            return "대화 중인 상대입니다.";
        }

        matchingRepository.save(Matching.builder()
                .hostId(user.getId())
                .guestId(guestId)
                .build());
        return "신청이 완료되었습니다.";
    }

    // 매칭 신청 거절 혹은 수락 -> 매칭 리스트에서 지우기
    @Transactional
    public String receiveMatching(User user, Long hostId, boolean accept) {

        User host = userRepository.findById(hostId).orElseThrow(
                () -> new NullPointerException("해당 신청을 주신 유저분은 존재하지 않습니다.")
        );

        Matching matching = matchingRepository.findByHostIdAndGuestId(hostId, user.getId()).orElseThrow(
                () -> new IllegalArgumentException("잘못된 정보입니다.")
        );

        if (!accept) {
            matchingRepository.delete(matching);
            return user.getNickname() + "님이 " + host.getNickname() + "님의 신청을 거절하셨습니다.";
        }

        matchingRepository.delete(matching);

        return user.getNickname() + "님이 " + host.getNickname() + "님의 신청을 수락하셨습니다.";
    }
}
