package com.sparta.mbti.service;

<<<<<<< HEAD
import com.sparta.mbti.dto.MatchingResponseDto;
=======
import com.sparta.mbti.dto.response.MatchResponseDto;
>>>>>>> develop
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
<<<<<<< HEAD
     3. 매치 신청 보내기*/
    @Transactional
=======
     3. 매칭 신청 보내기*/
>>>>>>> develop
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

<<<<<<< HEAD
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
=======
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

    // host가 guest내역 조회
    @Transactional
    public List<MatchResponseDto> sentMatching(User user) {
        List<Matching> matchList = matchingRepository.findAllByHostId(user.getId());

        List<MatchResponseDto> sentList = new ArrayList<>();
        for (Matching oneMatch : matchList ) {

            User guest = userRepository.findById(oneMatch.getGuestId()).orElseThrow(
                    () -> new NullPointerException("유저의 정보가 없습니다.")
            );

            sentList.add(MatchResponseDto.builder()
                    .matchingId(oneMatch.getId())
                    .hostId(oneMatch.getHostId())
                    .guestId(guest.getId())
                    .partnerNick(guest.getNickname())
                    .partnerImg(guest.getProfileImage())
                    .partnerMbti(guest.getMbti().getMbti())
                    .partnerIntro(guest.getIntro())
                    .build());
        }
        return sentList;
    }

    // guest가 host내역 조회
    @Transactional
    public List<MatchResponseDto> invitedMatching(User user) {
        List<Matching> matchList = matchingRepository.findAllByGuestId(user.getId());

        List<MatchResponseDto> invitations = new ArrayList<>();
        for (Matching oneMatch : matchList) {

            User host = userRepository.findById(oneMatch.getHostId()).orElseThrow(
                    () -> new NullPointerException("유저의 정보가 없습니다.")
            );

            invitations.add(MatchResponseDto.builder()
                    .matchingId(oneMatch.getId())
                    .hostId(oneMatch.getHostId())
                    .guestId(oneMatch.getGuestId())
                    .partnerNick(host.getNickname())
                    .partnerImg(host.getProfileImage())
                    .partnerMbti(host.getMbti().getMbti())
                    .partnerIntro(host.getIntro())
                    .build());
        }
        return invitations;
    }

    public String deleteMatching(User user, Long guestId) {
        Matching matching = matchingRepository.findByHostIdAndGuestId(user.getId(), guestId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 정보입니다.")
        );

        matchingRepository.delete(matching);

        return "신청이 취소되었습니다.";
>>>>>>> develop
    }
}
