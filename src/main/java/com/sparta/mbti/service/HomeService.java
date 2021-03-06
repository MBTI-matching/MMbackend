package com.sparta.mbti.service;

import com.sparta.mbti.dto.response.*;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.*;
import com.sparta.mbti.utils.CardinalDirection;
import com.sparta.mbti.utils.GeometryUtils;
import com.sparta.mbti.utils.Radius;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    private final LocDetailRepository locDetailRepository;
    private final MbtiRepository mbtiRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserInterestRepository userInterestRepository;
    private final EntityManager entityManager;

    // 내위치 2km 반경 케미 리스트 (위치 / MBTI)
    @Transactional
    public ChemyAllResponseDto chemyList(User user) {
        Long baseUserId = user.getId();                     // 쿼리 조건 (본인 제외)
        String baseMbti = user.getMbti().getMbti();         // 쿼리 조건 (mbti 궁합 4개까지)
        Long baseLocationId = user.getLocation().getId();   // 쿼리 조건 (본인 위치 지역)

        double baseLatitude = Double.parseDouble(user.getLatitude());
        double baseLongitude = Double.parseDouble(user.getLongitude());
        double distance = 2;    // 2Km

        // 북동쪽 좌표 구하기
        Radius northEast = GeometryUtils.calculateByDirection(baseLatitude, baseLongitude, distance, CardinalDirection.NORTHEAST.getBearing());
        // 남서쪽 좌표 구하기
        Radius southWest = GeometryUtils.calculateByDirection(baseLatitude, baseLongitude, distance, CardinalDirection.SOUTHWEST.getBearing());

        double x1 = northEast.getLongitude();
        double y1 = northEast.getLatitude();
        double x2 = southWest.getLongitude();
        double y2 = southWest.getLatitude();

        // native query 활용
        Query query = entityManager.createNativeQuery(
                        "SELECT * " +
                                "FROM user AS u, mbti AS m " +
                                "WHERE u.mbti_id = m.mbti_id " +
                                "AND MBRContains(ST_LINESTRINGFROMTEXT(" + String.format("'LINESTRING(%f %f, %f %f)')", x1, y1, x2, y2) + ", u.point) " +
                                "AND u.user_id != ? " +         // 본인 제외
                                "AND u.location_id = ? " +      // 본인 위치 지역
                                "AND (m.mbti_first = ? OR m.mbti_second = ? OR m.mbti_third = ? OR m.mbti_forth = ?) "  // mbti 궁합 4개까지
                        , User.class)
                .setParameter(1, baseUserId)
                .setParameter(2, baseLocationId)
                .setParameter(3, baseMbti)
                .setParameter(4, baseMbti)
                .setParameter(5, baseMbti)
                .setParameter(6, baseMbti);

        List<User> findUserList = query.getResultList();

        if (!findUserList.isEmpty()) {
            // 반환 사용자 리스트
            List<ChemyUserResponseDto> chemyUserListDtos = new ArrayList<>();
            // 사이
            for (User oneUser : findUserList) {
                // 관심사 리스트 조회
                List<UserInterest> userInterestList = userInterestRepository.findAllByUser(oneUser);
                List<String> interestList = new ArrayList<>();
                for (UserInterest userInterest : userInterestList) {
                    interestList.add(userInterest.getInterest().getInterest());
                }

                // 상성 표기
                String affinity;

                Mbti userMbti = user.getMbti();
                Mbti oneMbti = oneUser.getMbti();

                if (userMbti.getMbti().equals(oneMbti.getMbtiFirst())) {
                    affinity = "우리는 소울메이트!";
                } else if (userMbti.getMbti().equals(oneMbti.getMbtiSecond()) || userMbti.getMbti().equals(oneMbti.getMbtiThird()) || userMbti.getMbti().equals(oneMbti.getMbtiForth())) {
                    affinity = "친해지기 쉬운 사이입니다.";
                } else {
                    affinity = "무난한 사이입니다.";
                }

                chemyUserListDtos.add(ChemyUserResponseDto.builder()
                        .userId(oneUser.getId())
                        .nickname(oneUser.getNickname())
                        .profileImage(oneUser.getProfileImage())
                        .intro(oneUser.getIntro())
                        .location(oneUser.getLocation().getLocation())
                        .locDetail(oneUser.getLocDetail().getLocDetail())
                        .mbti(oneUser.getMbti().getMbti())
                        .affinity(affinity)
                        .interestList(interestList)
                        .detail(oneUser.getMbti().getDetail())
                        .build());
            }

            // 반환
            return ChemyAllResponseDto.builder()
                    .location(user.getLocation().getLocation())
                    .locDetail(user.getLocDetail().getLocDetail())
                    .userCount(findUserList.size())
                    .userList(chemyUserListDtos)
                    .build();
        }
        return ChemyAllResponseDto.builder()
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
            // 해당 상세위치 조회
            List<LocDetail> locDetailList = locDetailRepository.findAllByLocation(location);

            Random generatorLocDetail = new Random();
            int locDetailSize = locDetailList.size();

            if (locDetailSize > 0) {
                LocDetail locDetail = locDetailRepository.findById(locDetailList.get(generatorLocDetail.nextInt(locDetailSize)).getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 상세위치가 존재하지 않습니다.")
                        );
                // 해당 위치에서 사용자 리스트
                // limit query
                List<User> userList = userRepository.findAllByLocationAndLocDetail(location, locDetail);
                Random generatorUser = new Random();
                // 사용자 리스트 최대 10명으로 설정, 10명 미만이면 해당 리스트 갯수로 설정
                int maxCount = userList.size();
                if (maxCount >= 10) {
                    maxCount = 10;
                }
                // 사용자 존재하면
                if (maxCount > 0) {
                    List<ChemyUserResponseDto> chemyUserListDtos = new ArrayList<>();
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
                        List<String> interestList = new ArrayList<>();
                        for (UserInterest userInterest : userInterestList) {
                            interestList.add(userInterest.getInterest().getInterest());
                        }

                        chemyUserListDtos.add(ChemyUserResponseDto.builder()
                                .userId(findUser.getId())
                                .nickname(findUser.getNickname())
                                .profileImage(findUser.getProfileImage())
                                .intro(findUser.getIntro())
                                .location(findUser.getLocation().getLocation())
                                .locDetail(findUser.getLocDetail().getLocDetail())
                                .mbti(findUser.getMbti().getMbti())
                                .interestList(interestList)
                                .affinity("로그인이 필요합니다.")
                                .detail(findUser.getMbti().getDetail())
                                .build());
                    }
                    // 반환
                    return ChemyAllResponseDto.builder()
                            .location(location.getLocation())
                            .locDetail(locDetail.getLocDetail())
                            .userCount(userList.size())
                            .userList(chemyUserListDtos)
                            .build();
                }
            }
        }
        return ChemyAllResponseDto.builder()
                .build();
    }

    // 지역 케미 리스트 (위치 / MBTI)
    public ChemyAllResponseDto locationList(Long locationId, Long locDetailId, User user) {
        // 위치 조회
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new NullPointerException("해당 위치가 존재하지 않습니다.")
        );

        // 상세위치 조회
        LocDetail locDetail = locDetailRepository.findById(locDetailId).orElseThrow(
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
        List<User> findUserList = userRepository.findAllByLocationAndLocDetailAndMbtiIn(location, locDetail, findMbtiList);
        // 반환 사용자 리스트
        List<ChemyUserResponseDto> chemyUserListDtos = new ArrayList<>();
        // 사이
        String affinity;
        for (User oneUser : findUserList) {

            Mbti oneMbti = oneUser.getMbti();
            Mbti userMbti = user.getMbti();

            if (userMbti.getMbti().equals(oneMbti.getMbtiFirst())) {
                affinity = "우리는 소울메이트!";
            } else if (userMbti.getMbti().equals(oneMbti.getMbtiSecond()) || userMbti.getMbti().equals(oneMbti.getMbtiThird()) || userMbti.getMbti().equals(oneMbti.getMbtiForth())) {
                affinity = "친해지기 쉬운 사이입니다.";
            } else {
                affinity = "무난한 사이입니다.";
            }

            // 관심사 리스트 조회
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(oneUser);
            List<String> interestList = new ArrayList<>();
            for (UserInterest userInterest : userInterestList) {
                interestList.add(userInterest.getInterest().getInterest());
            }

            chemyUserListDtos.add(ChemyUserResponseDto.builder()
                    .userId(oneUser.getId())
                    .nickname(oneUser.getNickname())
                    .profileImage(oneUser.getProfileImage())
                    .intro(oneUser.getIntro())
                    .location(oneUser.getLocation().getLocation())
                    .locDetail(oneUser.getLocDetail().getLocDetail())
                    .mbti(oneUser.getMbti().getMbti())
                    .affinity(affinity)
                    .interestList(interestList)
                    .detail(oneUser.getMbti().getDetail())
                    .build());
        }

        // 반환
        return ChemyAllResponseDto.builder()
                .location(location.getLocation())
                .locDetail(locDetail.getLocDetail())
                .userCount(findUserList.size())
                .userList(chemyUserListDtos)
                .build();
    }

    // 전체 게시글
    @Transactional
    public List<PostResponseDto> getAllposts(Pageable pageable, User user) {

        // 사이
        String affinity;

        // page, size, 내림차순으로 페이징한 게시글 리스트
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        // 반환할 게시글 리스트
        List<PostResponseDto> posts = new ArrayList<>();
        for (Post onePost : postList) {
            // 게시글 좋아요 수
            int likesCount = (int) likesRepository.findAllByPost(onePost).size();
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
                    .locDetail(onePost.getUser().getLocDetail().getLocDetail())
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
    public ChemyAllResponseDto interestList(Long locationId, Long locDetailId, Long interestId, User user) {
        // 위치 조회
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new NullPointerException("해당 위치가 존재하지 않습니다.")
        );

        // 상세위치 조회
        LocDetail locDetail = locDetailRepository.findById(locDetailId).orElseThrow(
                () -> new NullPointerException("해당 상세위치가 존재하지 않습니다.")
        );

        // 관심사 조회
        Interest interest = interestRepository.findById(interestId).orElseThrow(
                () -> new NullPointerException("해당 관심사는 존재하지 않습니다.")
        );

        // 지역별 사용자 리스트
        List<User> userList = userRepository.findAllByLocationAndLocDetail(location, locDetail);

        // 같은 관심사를 지닌 유저 골라내기
        List<User> interestedUser = new ArrayList<>();
        for (User finduser : userList) {

            int maxInterest = finduser.getUserInterestList().size();
            for (int i = 0; i < maxInterest; i++)

                if (finduser.getUserInterestList().get(i).getInterest().getInterest().equals(interest.getInterest())) {
                    interestedUser.add(finduser);
                }
        }

        // 사용자 리스트 최대 10명으로 설정, 10명 미만이면 해당 리스트 갯수로 설정
        Random generatorUser = new Random();
        int maxCount = interestedUser.size();
        if (maxCount >= 10) {
            maxCount = 10;
        }

        List<ChemyUserResponseDto> chemyUserListDtos = new ArrayList<>();
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

        String affinity;

        for (int i = 0; i < maxCount; i++) {
            User findUser = interestedUser.get(i);
            Mbti findMbti = findUser.getMbti();
            Mbti userMbti = user.getMbti();

            if (userMbti.getMbti().equals(findMbti.getMbtiFirst())) {
                affinity = "우리는 소울메이트!";
            } else if (userMbti.getMbti().equals(findMbti.getMbtiSecond()) || userMbti.getMbti().equals(findMbti.getMbtiThird()) || userMbti.getMbti().equals(findMbti.getMbtiForth())) {
                affinity = "친해지기 쉬운 사이입니다.";
            } else {
                affinity = "무난한 사이입니다.";
            }
            // 관심사 리스트 조회
            List<String> interestList = new ArrayList<>();
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
            for (UserInterest usersInterest : userInterestList) {
                interestList.add(usersInterest.getInterest().getInterest());
            }

            chemyUserListDtos.add(ChemyUserResponseDto.builder()
                    .userId(findUser.getId())
                    .nickname(findUser.getNickname())
                    .profileImage(findUser.getProfileImage())
                    .intro(findUser.getIntro())
                    .location(findUser.getLocation().getLocation())
                    .locDetail(findUser.getLocDetail().getLocDetail())
                    .mbti(findUser.getMbti().getMbti())
                    .affinity(affinity)
                    .detail(findUser.getMbti().getDetail())
                    .interestList(interestList)
                    .build());
        }
        return ChemyAllResponseDto.builder()
                .location(location.getLocation())
                .locDetail(locDetail.getLocDetail())
                .userCount(interestedUser.size())
                .userList(chemyUserListDtos)
                .build();
    }

    // 관심사별 케미 리스트 #2 (지역 / 관심사 / MBTI)
    public ChemyAllResponseDto chemyInterest(Long locationId, Long locDetailId, Long interestId, User user) {

        // 위치 조회
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new NullPointerException("해당 위치가 존재하지 않습니다.")
        );

        // 상세위치 조회
        LocDetail locDetail = locDetailRepository.findById(locDetailId).orElseThrow(
                () -> new NullPointerException("해당 상세위치가 존재하지 않습니다.")
        );

        // 관심사 조회
        Interest interest = interestRepository.findById(interestId).orElseThrow(
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
        List<User> userList = userRepository.findAllByLocationAndLocDetailAndMbtiIn(location, locDetail, findMbtiList);

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

        List<ChemyUserResponseDto> chemyUserListDtos = new ArrayList<>();
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

        // 사이
        String affinity;

        for (int i = 0; i < maxCount; i++) {
            User findUser = interestedUser.get(i);
            Mbti findMbti = findUser.getMbti();
            Mbti userMbti = user.getMbti();

            if (userMbti.getMbti().equals(findMbti.getMbtiFirst())) {
                affinity = "우리는 소울메이트!";
            } else if (userMbti.getMbti().equals(findMbti.getMbtiSecond()) || userMbti.getMbti().equals(findMbti.getMbtiThird()) || userMbti.getMbti().equals(findMbti.getMbtiForth())) {
                affinity = "친해지기 쉬운 사이입니다.";
            } else {
                affinity = "무난한 사이입니다.";
            }

            // 관심사 리스트 조회
            List<String> interestList = new ArrayList<>();
            List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
            for (UserInterest userInterest : userInterestList) {
                interestList.add(userInterest.getInterest().getInterest());
            }

            chemyUserListDtos.add(ChemyUserResponseDto.builder()
                    .userId(findUser.getId())
                    .nickname(findUser.getNickname())
                    .profileImage(findUser.getProfileImage())
                    .intro(findUser.getIntro())
                    .location(findUser.getLocation().getLocation())
                    .locDetail(findUser.getLocDetail().getLocDetail())
                    .mbti(findUser.getMbti().getMbti())
                    .affinity(affinity)
                    .interestList(interestList)
                    .detail(findUser.getMbti().getDetail())
                    .build());
        }
        return ChemyAllResponseDto.builder()
                .location(location.getLocation())
                .locDetail(locDetail.getLocDetail())
                .userCount(interestedUser.size())
                .userList(chemyUserListDtos)
                .build();
    }
}

