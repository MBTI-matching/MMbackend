package com.sparta.mbti.service;

import com.sparta.mbti.dto.MatchingResponseDto;
import com.sparta.mbti.model.Matching;
import com.sparta.mbti.model.Post;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.MatchingRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional
    public String requestMatching (User user, Long guestId){
        if(matchingRepository.existsByHostIdAndGuestId(user.getId(), guestId) || matchingRepository.existsByHostIdAndGuestId(guestId, user.getId())){
            return "신청 대기 상태입니다.";
        }

        if(chatRoomRepository.existsByHostIdAndGuestId(user.getId(), guestId) || chatRoomRepository.existsByHostIdAndGuestId(guestId, user.getId())){
            return "대화 중인 상대입니다.";
        }
        User guest = userRepository.findById(guestId).orElse(null);

        Matching matching = Matching.builder()
                .hostId(user.getId())
                .guestId(guestId)
                .build();

        matchingRepository.save(matching);

        user.setMatchingList(matching);
        guest.setMatchingList(matching);

        return "신청이 완료되었습니다.";
    }

    //매칭 보낸 내역
    @Transactional
    public List<MatchingResponseDto> sendMatching(User user){
        List<Matching> matchingList = matchingRepository.findAllByHostId(user.getId());
        List<MatchingResponseDto> matchingResponseDtoList = new ArrayList<>();
        for (Matching matching : matchingList){
            User guest = userRepository.findById(matching.getGuestId()).orElse(null);
            matchingResponseDtoList.add(MatchingResponseDto.builder()
                    .matchingId(matching.getId())
                    .guestId(matching.getGuestId())
                    .hostId(matching.getHostId())
                    .guestImg(guest.getProfileImage())
                    .guestMbti(guest.getMbti().getMbti())
                    .guestNick(guest.getNickname())
                    .build()
            );
        }
        return matchingResponseDtoList;
    }
    //매칭 받은 내역(상대방 닉네임, mbti, img)
    @Transactional
    public List<MatchingResponseDto> receiveMatching(User user){
        List<Matching> matchingList = matchingRepository.findAllByGuestId(user.getId());
        List<MatchingResponseDto> matchingResponseDtoList = new ArrayList<>();
        for (Matching matching : matchingList){
            User guest = userRepository.findById(matching.getGuestId()).orElse(null);
            matchingResponseDtoList.add(MatchingResponseDto.builder()
                    .matchingId(matching.getId())
                    .guestId(matching.getGuestId())
                    .hostId(matching.getHostId())
                    .guestImg(guest.getProfileImage())
                    .guestMbti(guest.getMbti().getMbti())
                    .guestNick(guest.getNickname())
                    .build()
            );
        }
        return matchingResponseDtoList;
    }

    @Transactional
    public void deleteMatching(User user, Long guestId) {
        if (matchingRepository.findByHostIdAndGuestId(user.getId(), guestId) != null)
            matchingRepository.delete(matchingRepository.findByHostIdAndGuestId(user.getId(), guestId));
        else
            matchingRepository.delete(matchingRepository.findByHostIdAndGuestId(guestId, user.getId()));
    }
}
