package com.sparta.mbti.service;

import com.sparta.mbti.dto.*;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class HomeService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final LocationRepository locationRepository;
    private final MbtiRepository mbtiRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserInterestRepository userInterestRepository;

    // 케미 리스트 (위치 / MBTI 케미)
    @Transactional
    public ChemyAllResponseDto chemyList(User user) {
        // MBTI 이상적 궁합 리스트 4개까지 조회
        String mbtiChemy = user.getMbti().getMbti();
        List<Mbti> findMbtiList = mbtiRepository.findAllByMbtiFirstOrMbtiSecondOrMbtiThirdOrMbtiForth(
                mbtiChemy,
                mbtiChemy,
                mbtiChemy,
                mbtiChemy);

        // 사용자 조회
        List<User> findUserList = userRepository.findAllByLocationAndMbtiIn(user.getLocation(), findMbtiList);
        // 반환 사용자 리스트
        List<ChemyUserListDto> chemyUserListDtos = new ArrayList<>();
        for (User oneUser : findUserList) {
            // 관심사 리스트 조회
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(oneUser);
            List<InterestListDto> interestList = new ArrayList<>();
            for (UserInterest userInterest : userInterestList) {
                interestList.add(InterestListDto.builder()
                        .interest(userInterest.getInterest().getInterest())
                        .build());
            }


            chemyUserListDtos.add(ChemyUserListDto.builder()
                                                .userId(oneUser.getId())
                                                .nickname(oneUser.getNickname())
                                                .profileImage(oneUser.getProfileImage())
                                                .intro(oneUser.getIntro())
                                                .location(oneUser.getLocation().getLocation())
                                                .mbti(oneUser.getMbti().getMbti())
                                                .interestList(interestList)
                                                .build());
        }

        // 반환
        return ChemyAllResponseDto.builder()
                                .location(user.getLocation().getLocation())
                                .longitude(user.getLocation().getLongitude())
                                .latitude(user.getLocation().getLatitude())
                                .userCount(findUserList.size())
                                .userList(chemyUserListDtos)
                                .build();
    }

    // 둘러보기
    public ChemyAllResponseDto chemyGuest() {
        // 위치 랜덤 생성
        List<Location> locationList = locationRepository.findAll();
        Random generatorLoc = new Random();
        int locSize = locationList.size();
        // 위차가 존재하면
        if (locSize > 0) {
            // 해당 위치 조회
            Location location = locationRepository.findById(locationList.get(generatorLoc.nextInt(locSize)).getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 위치가 존재하지 않습니다.")
                    );

            // 해당 위치에서 사용자 리스트
            List<User> userList = userRepository.findAllByLocation(location);
            Random generatorUser = new Random();
            // 사용자 리스트 최대 10명으로 설정, 10명 미만이면 해당 리스트 갯수로 설정
            int maxCount = userList.size();
            if (maxCount >= 10) {
                maxCount = 10;
            }
            // 사용자 존재하면
            if (maxCount > 0) {
                List<ChemyUserListDto> chemyUserListDtos = new ArrayList<>();
                // 사용자 리스트 인덱스 값 저장
                int[] userSize = new int[maxCount];
                for (int i = 0; i < maxCount; i++) {
                    userSize[i] = generatorUser.nextInt(maxCount);  // 랜덤 변수 생성
                    // 중복 제거
                    for (int j = 0; j < i; j++) {
                        if (userSize[i] == userSize[j]) {
                            i--;
                            break;
                        }
                    }
                }

                for (int i = 0; i < maxCount; i++) {
                    User findUser = userList.get(i);

                    // 관심사 리스트 조회
                    List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
                    List<InterestListDto> interestList = new ArrayList<>();
                    for (UserInterest userInterest : userInterestList) {
                        interestList.add(InterestListDto.builder()
                                .interest(userInterest.getInterest().getInterest())
                                .build());
                    }

                    chemyUserListDtos.add(ChemyUserListDto.builder()
                            .userId(findUser.getId())
                            .nickname(findUser.getNickname())
                            .profileImage(findUser.getProfileImage())
                            .intro(findUser.getIntro())
                            .location(findUser.getLocation().getLocation())
                            .mbti(findUser.getMbti().getMbti())
                            .interestList(interestList)
                            .build());
                }
                // 반환
                return ChemyAllResponseDto.builder()
                        .location(location.getLocation())
                        .longitude(location.getLongitude())
                        .latitude(location.getLatitude())
                        .userCount(userList.size())
                        .userList(chemyUserListDtos)
                        .build();
            }
        }
        return ChemyAllResponseDto.builder()
                .build();
    }

    // 지역 케미 리스트 (위치 / MBTI)
    public ChemyAllResponseDto locationList(Long locationId, User user) {
        // 위치 조회
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new NullPointerException("해당 위치가 존재하지 않습니다.")
        );

        // MBTI 이상적 궁합 리스트 4개까지 조회
        String mbtiChemy = user.getMbti().getMbti();
        List<Mbti> findMbtiList = mbtiRepository.findAllByMbtiFirstOrMbtiSecondOrMbtiThirdOrMbtiForth(
                mbtiChemy,
                mbtiChemy,
                mbtiChemy,
                mbtiChemy);

        // 사용자 조회
        List<User> findUserList = userRepository.findAllByLocationAndMbtiIn(location, findMbtiList);
        // 반환 사용자 리스트
        List<ChemyUserListDto> chemyUserListDtos = new ArrayList<>();
        for (User oneUser : findUserList) {
            // 관심사 리스트 조회
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(oneUser);
            List<InterestListDto> interestList = new ArrayList<>();
            for (UserInterest userInterest : userInterestList) {
                interestList.add(InterestListDto.builder()
                        .interest(userInterest.getInterest().getInterest())
                        .build());
            }

            chemyUserListDtos.add(ChemyUserListDto.builder()
                    .userId(oneUser.getId())
                    .nickname(oneUser.getNickname())
                    .profileImage(oneUser.getProfileImage())
                    .intro(oneUser.getIntro())
                    .location(oneUser.getLocation().getLocation())
                    .mbti(oneUser.getMbti().getMbti())
                    .interestList(interestList)
                    .build());
        }

        // 반환
        return ChemyAllResponseDto.builder()
                .location(location.getLocation())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .userCount(findUserList.size())
                .userList(chemyUserListDtos)
                .build();
    }
    // 전체 게시글

    @Transactional
    public List<PostResponseDto> getAllposts(Pageable pageable, User user) {
        // page, size, 내림차순으로 페이징한 게시글 리스트
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        // 반환할 게시글 리스트
        List<PostResponseDto> posts = new ArrayList<>();
        for (Post onePost : postList) {
            // 게시글 좋아요 수
            int likesCount = (int)likesRepository.findAllByPost(onePost).size();
            // 게시글 좋아요 여부
            boolean likeStatus = likesRepository.existsByUserAndPost(user, onePost);
            // 게시글 이미지 리스트
            List<Image> imageList = imageRepository.findAllByPost(onePost);
            // 반환할 이미지 리스트
            List<ImageResponseDto> images = new ArrayList<>();
            for (Image oneImage : imageList) {
                images.add(ImageResponseDto.builder()
                        .imageId(oneImage.getId())
                        .imageLink(oneImage.getImageLink())
                        .build());
            }

            // 게시글에 달린 댓글 리스트 (작성일자 오름차순)
            List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtAsc(onePost);
            // 반환할 댓글 리스트
            List<CommentResopnseDto> comments = new ArrayList<>();
            for (Comment oneComment : commentList) {
                comments.add(CommentResopnseDto.builder()
                                            .commentId(oneComment.getId())
                                            .nickname(oneComment.getUser().getNickname())
                                            .image(oneComment.getUser().getProfileImage())
                                            .mbti(oneComment.getUser().getMbti().getMbti())
                                            .comment(oneComment.getComment())
                                            .createdAt(oneComment.getCreatedAt())
                                            .build());
            }
            // 전체 게시글 리스트
            posts.add(PostResponseDto.builder()
                                .postId(onePost.getId())
                                .nickname(onePost.getUser().getNickname())
                                .profileImage(onePost.getUser().getProfileImage())
                                .location(onePost.getUser().getLocation().getLocation())
                                .mbti(onePost.getUser().getMbti().getMbti())
                                .content(onePost.getContent())
                                .tag(onePost.getTag())
                                .likesCount(likesCount)
                                .likeStatus(likeStatus)
                                .imageList(images)
                                .commentList(comments)
                                .createdAt(onePost.getCreatedAt())
                                .build());
        }
        return posts;
    }

    // 관심사별 케미 리스트 #1 (지역 / 관심사)
    public ChemyAllResponseDto interestList(Long locationId, Long interestId) {
        // 위치 조회
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new NullPointerException("해당 위치가 존재하지 않습니다.")
        );

        // 관심사 조회
        Interest interest =interestRepository.findById(interestId).orElseThrow(
                () -> new NullPointerException("해당 관심사는 존재하지 않습니다.")
        );

        // 지역별 사용자 리스트
        List<User> userList = userRepository.findAllByLocation(location);

        // 같은 관심사를 지닌 유저 골라내기
        List<User> interestedUser = new ArrayList<>();
        for (User user : userList) {

            int maxInterest = user.getUserInterestList().size();
            for (int i = 0; i < maxInterest; i++)

            if (user.getUserInterestList().get(i).getInterest().getInterest().equals(interest.getInterest())) {
                interestedUser.add(user);
            }
        }

        // 사용자 리스트 최대 10명으로 설정, 10명 미만이면 해당 리스트 갯수로 설정
        Random generatorUser = new Random();
        int maxCount = interestedUser.size();
        if (maxCount >= 10) {
            maxCount = 10;
        }

        List<ChemyUserListDto> chemyUserListDtos = new ArrayList<>();
        // 사용자 리스트 인덱스 값 저장
        int[] userSize = new int[maxCount];
        for (int i = 0; i < maxCount; i++) {
            userSize[i] = generatorUser.nextInt(maxCount);  // 랜덤 변수 생성
            // 중복 제거
            for (int j = 0; j < i; j++) {
                if (userSize[i] == userSize[j]) {
                    i--;
                    break;
                }
            }
        }

        for (int i = 0; i < maxCount; i++) {
            User findUser = interestedUser.get(i);

            // 관심사 리스트 조회
            List<InterestListDto> interestList = new ArrayList<>();
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
            for (UserInterest usersInterest : userInterestList) {
                interestList.add(InterestListDto.builder()
                        .interest(usersInterest.getInterest().getInterest())
                        .build());
            }

            chemyUserListDtos.add(ChemyUserListDto.builder()
                    .userId(findUser.getId())
                    .nickname(findUser.getNickname())
                    .profileImage(findUser.getProfileImage())
                    .intro(findUser.getIntro())
                    .location(findUser.getLocation().getLocation())
                    .mbti(findUser.getMbti().getMbti())
                    .interestList(interestList)
                    .build());
        }
        return ChemyAllResponseDto.builder()
                .location(location.getLocation())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .userCount(interestedUser.size())
                .userList(chemyUserListDtos)
                .build();
    }

    // 관심사별 케미 리스트 #2 (지역 / 관심사 / MBTI)
    public ChemyAllResponseDto chemyInterest(Long locationId, Long interestId, User user) {

        // 위치 조회
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new NullPointerException("해당 위치가 존재하지 않습니다.")
        );

        // 관심사 조회
        Interest interest =interestRepository.findById(interestId).orElseThrow(
                () -> new NullPointerException("해당 관심사는 존재하지 않습니다.")
        );

        // MBTI 이상적 궁합 리스트 4개까지 조회
        String mbtiChemy = user.getMbti().getMbti();
        List<Mbti> findMbtiList = mbtiRepository.findAllByMbtiFirstOrMbtiSecondOrMbtiThirdOrMbtiForth(
                mbtiChemy,
                mbtiChemy,
                mbtiChemy,
                mbtiChemy);

        // 지역별 사용자 리스트
        List<User> userList = userRepository.findAllByLocationAndMbtiIn(location, findMbtiList);

        // 같은 관심사를 지닌 유저 골라내기
        List<User> interestedUser = new ArrayList<>();
        for (User someUser : userList) {

            int maxInterest = someUser.getUserInterestList().size();
            for (int i = 0; i < maxInterest; i++)

                if (someUser.getUserInterestList().get(i).getInterest().getInterest().equals(interest.getInterest())) {
                    interestedUser.add(someUser);
                }
        }

        // 사용자 리스트 최대 10명으로 설정, 10명 미만이면 해당 리스트 갯수로 설정
        Random generatorUser = new Random();
        int maxCount = interestedUser.size();
        if (maxCount >= 10) {
            maxCount = 10;
        }

        List<ChemyUserListDto> chemyUserListDtos = new ArrayList<>();
        // 사용자 리스트 인덱스 값 저장
        int[] userSize = new int[maxCount];
        for (int i = 0; i < maxCount; i++) {
            userSize[i] = generatorUser.nextInt(maxCount);  // 랜덤 변수 생성
            // 중복 제거
            for (int j = 0; j < i; j++) {
                if (userSize[i] == userSize[j]) {
                    i--;
                    break;
                }
            }
        }

        for (int i = 0; i < maxCount; i++) {
            User findUser = interestedUser.get(i);

            // 관심사 리스트 조회
            List<InterestListDto> interestList = new ArrayList<>();
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
            for (UserInterest userInterest : userInterestList) {
                interestList.add(InterestListDto.builder()
                        .interest(userInterest.getInterest().getInterest())
                        .build());
            }

            chemyUserListDtos.add(ChemyUserListDto.builder()
                    .userId(findUser.getId())
                    .nickname(findUser.getNickname())
                    .profileImage(findUser.getProfileImage())
                    .intro(findUser.getIntro())
                    .location(findUser.getLocation().getLocation())
                    .mbti(findUser.getMbti().getMbti())
                    .interestList(interestList)
                    .build());
        }
        return ChemyAllResponseDto.builder()
                .location(location.getLocation())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .userCount(interestedUser.size())
                .userList(chemyUserListDtos)
                .build();
    }
}

