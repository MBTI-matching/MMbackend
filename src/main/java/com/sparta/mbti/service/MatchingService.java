package com.sparta.mbti.service;


import com.sparta.mbti.dto.response.MatchResponseDto;
import com.sparta.mbti.model.Matching;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.MatchingRepository;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        User guest = userRepository.findById(guestId).orElseThrow(
                () -> new NullPointerException("유저 정보가 존재하지 않습니다."));
        List<User> admin = userRepository.findAllByRole(User.Role.ROLE_ADMIN);

        Matching matching;
        // 0 ~ 1까지의 숫자 랜덤 반환
        // 관리자 중복 안되게
        int rand = (int)(Math.random() * admin.size());
        if(guest.getRole().equals(User.Role.ROLE_BOT)){
            matching = Matching.builder()
                    .hostId(user.getId())
                    .guestId(admin.get(rand).getId())
                    .build();}
        else {
            matching = Matching.builder()
                    .hostId(user.getId())
                    .guestId(guestId)
                    .build();
        }
        
        if(matchingRepository.existsByHostIdAndGuestId(matching.getHostId(), matching.getGuestId()) ||
                matchingRepository.existsByHostIdAndGuestId(matching.getGuestId(), matching.getHostId())){
            return "신청 대기 상태입니다.";
        }

        if(chatRoomRepository.existsByHostIdAndGuestId(matching.getHostId(), matching.getGuestId()) ||
                chatRoomRepository.existsByHostIdAndGuestId(matching.getGuestId(), matching.getHostId())){
            return "대화 중인 상대입니다.";
        }

        if(guestId.equals(user.getId()))
            return "본인과의 매칭은 불가능합니다.";

        matchingRepository.save(matching);

        return "신청이 완료되었습니다.";
    }

    // 매칭 신청 거절 혹은 수락 -> 매칭 리스트에서 지우기
    @Transactional
    public String receiveMatching(User user, Long hostId, boolean accept) {

        User host = userRepository.findById(hostId).orElseThrow(
                () -> new NullPointerException("해당 신청을 주신 유저분은 존재하지 않습니다.")
        );

        Matching matching = matchingRepository.findByHostIdAndGuestId(hostId, user.getId());

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
        Matching matching = null;
        //초대 했을 경우 신청 취소
        if(matchingRepository.existsByHostIdAndGuestId(user.getId(), guestId)) {
            matching = matchingRepository.findByHostIdAndGuestId(user.getId(), guestId);
        }
        //초대 받았을 경우 신청 취소
        else if(matchingRepository.existsByHostIdAndGuestId(guestId, user.getId())){
            matching = matchingRepository.findByHostIdAndGuestId(guestId, user.getId());
        }
        else
            return "잘못된 요청입니다";
        matchingRepository.delete(matching);

        return "신청이 취소되었습니다.";
    }
}
