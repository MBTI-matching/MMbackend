package com.sparta.mbti.service;

import com.sparta.mbti.model.Matching;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.MatchingRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MatchingRepository matchingRepository;

     /*매칭 신청
     1. 매칭 신청 대기 상태인지 확인
     2. 이미 매칭이 성사되어 채팅중인 상태인지 확인
     3. 매치 신청 보내기*/
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

}
