package com.sparta.mbti.service;


import com.sparta.mbti.dto.response.MatchResponseDto;
import com.sparta.mbti.model.Matching;
import com.sparta.mbti.model.Mbti;
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

    // 보낸 내역 조회
    @Transactional
    public List<MatchResponseDto> sentMatching(User user) { //user == host
        List<Matching> matchList = matchingRepository.findAllByHostId(user.getId());

        List<MatchResponseDto> sentList = new ArrayList<>();
        for (Matching match : matchList) {
            User partner;
            if(userRepository.existsById(match.getGuestId())){
                partner = userRepository.findById(match.getGuestId()).orElseThrow(
                        () -> new NullPointerException("유저의 정보가 없습니다.")
                );
                // 상성 표기
                String affinity;
                Mbti userMbti = user.getMbti();
                Mbti partnerMbti = partner.getMbti();

                if (userMbti.getMbti().equals(partnerMbti.getMbtiFirst())) {
                    affinity = "우리는 소울메이트!";
                } else if (userMbti.getMbti().equals(partnerMbti.getMbtiSecond()) || userMbti.getMbti().equals(partnerMbti.getMbtiThird()) || userMbti.getMbti().equals(partnerMbti.getMbtiForth())) {
                    affinity = "친해지기 쉬운 사이입니다.";
                } else {
                    affinity = "무난한 사이입니다.";
                }
                sentList.add(MatchResponseDto.builder()
                        .partnerId(partner.getId())
                        .partnerNick(partner.getNickname())
                        .partnerImg(partner.getProfileImage())
                        .partnerMbti(partner.getMbti().getMbti())
                        .partnerIntro(partner.getIntro())
                        .affinity(affinity)
                        .build());
            }
            else
                sentList.add(MatchResponseDto.builder()
                        .partnerId(0L)
                        .partnerNick("없는 상대입니다.")
                        .partnerImg("https://bizchemy-bucket-s3.s3.ap-northeast-2.amazonaws.com/default/default.png")
                        .partnerMbti("ENTJ")
                        .partnerIntro("없는 상대입니다.")
                        .build());
        }
        return sentList;
    }

    // 받은 내역 조회
    @Transactional
    public List<MatchResponseDto> invitedMatching(User user) {  // User = guest
        List<Matching> matchList = matchingRepository.findAllByGuestId(user.getId());

        // MBTI 상성
        String affinity;

        List<MatchResponseDto> invitations = new ArrayList<>();
        for (Matching match : matchList) {
            User partner;
            if (userRepository.existsById(match.getHostId())) {
                partner = userRepository.findById(match.getHostId()).orElseThrow(
                        () -> new NullPointerException("유저의 정보가 없습니다.")
                );

                Mbti userMbti = user.getMbti();
                Mbti partnerMbti = partner.getMbti();

                if (userMbti.getMbti().equals(partnerMbti.getMbtiFirst())) {
                    affinity = "우리는 소울메이트!";
                } else if (userMbti.getMbti().equals(partnerMbti.getMbtiSecond()) || userMbti.getMbti().equals(partnerMbti.getMbtiThird()) || userMbti.getMbti().equals(partnerMbti.getMbtiForth())) {
                    affinity = "친해지기 쉬운 사이입니다.";
                } else {
                    affinity = "무난한 사이입니다.";
                }

                invitations.add(MatchResponseDto.builder()
                        .partnerId(partner.getId())
                        .partnerNick(partner.getNickname())
                        .partnerImg(partner.getProfileImage())
                        .partnerMbti(partner.getMbti().getMbti())
                        .partnerIntro(partner.getIntro())
                        .affinity(affinity)
                        .build());
            }
            // 상대가 없는경우
            else
                invitations.add(MatchResponseDto.builder()
                        .partnerId(0L)
                        .partnerNick("없는 상대입니다.")
                        .partnerImg("https://bizchemy-bucket-s3.s3.ap-northeast-2.amazonaws.com/default/default.png")
                        .partnerMbti("ENTJ")
                        .partnerIntro("없는 상대입니다.")
                        .affinity("없는 상대입니다.")
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
