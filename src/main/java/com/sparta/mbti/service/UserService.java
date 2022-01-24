package com.sparta.mbti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mbti.dto.request.KakaoUserRequestDto;
import com.sparta.mbti.dto.request.UserRequestDto;
import com.sparta.mbti.dto.response.*;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.*;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.security.jwt.JwtTokenUtils;
import com.sparta.mbti.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationRepository locationRepository;
    private final LocDetailRepository locDetailRepository;
    private final MbtiRepository mbtiRepository;
    private final InterestRepository interestRepository;
    private final UserInterestRepository userInterestRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final LikesRepository likesRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final S3Uploader s3Uploader;
    private final MatchingRepository matchingRepository;

    private final String imageDirName = "user";   // S3 폴더 경로
    static boolean signStatus = false;      // 회원가입 상태

    public UserResponseDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserRequestDto kakaoUserRequestDto = getKakaoUserInfo(accessToken);

        // 3. "카카오 사용자 정보"로 필요시 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserRequestDto);

        // 4. 강제 로그인 처리
        return forceLogin(kakaoUser, response);
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "5d14d9239c0dbefee951a1093845427f");                  // 개발 REST API 키
//        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");      // 개발 Redirect URI
//        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");      // 개발 Redirect URImatching
        body.add("redirect_uri", "https://www.bizchemy.com/user/kakao/callback");      // 개발 Redirect URI

        body.add("code", code);


        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        // JSON -> Java Object
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private KakaoUserRequestDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);      // JWT 토큰
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        // JSON -> Java Object
        // 이 부분에서 카톡 프로필 정보 가져옴
        JSONObject body = new JSONObject(response.getBody());
        System.out.println(body);

        // ID (카카오 기본키)
        Long id = body.getLong("id");
        // 아이디 (이메일)
        String username = body.getJSONObject("kakao_account").getString("email");
        // 닉네임
        String nickname = body.getJSONObject("properties").getString("nickname");

        // profile_image_needs_agreement: true (이미지 동의 안함), false (이미지 동의)
        // is_default_image: true (기본 이미지), false (이미지 등록됨)
        // 프로필 이미지
        String profileImage = "https://bizchemy-bucket-s3.s3.ap-northeast-2.amazonaws.com/default/default.png";
        // 이미지 동의 및 등록 되었으면
        if (!body.getJSONObject("kakao_account").getBoolean("profile_image_needs_agreement") &&
                !body.getJSONObject("kakao_account").getJSONObject("profile").getBoolean("is_default_image")) {
            profileImage = body.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url");
        }

        // has_gender: false (성별 선택 안함), true (성별 선택)
        // gender_needs_agreement: true (성별 동의 안함), false (성별 동의)
        // 성별 (male, female, unchecked)
        String gender = "";
        // 성별 선택 및 성별 동의 되었으면
        if (body.getJSONObject("kakao_account").getBoolean("has_gender") &&
                !body.getJSONObject("kakao_account").getBoolean("gender_needs_agreement")) {
            gender = body.getJSONObject("kakao_account").getString("gender");
        }

        // has_age_range: false (연령대 선택 안함), true (연령대 선택)
        // age_range_needs_agreement: true (연령대 동의 안함), false (연령대 동의)
        // 연령대
        String ageRange = "";
        // 이미지 동의 및 등록 되었으면
        if (body.getJSONObject("kakao_account").getBoolean("has_age_range") &&
                !body.getJSONObject("kakao_account").getBoolean("age_range_needs_agreement")) {
            ageRange = body.getJSONObject("kakao_account").getString("age_range");
        }

        return KakaoUserRequestDto.builder()
                .id(id)
                .username(username)
                .nickname(nickname)
                .profileImage(profileImage)
                .gender(gender)
                .ageRange(ageRange)
                .build();
    }

    // 3. "카카오 사용자 정보"로 필요시 회원가입
    private User registerKakaoUserIfNeeded(KakaoUserRequestDto kakaoUserRequestDto) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserRequestDto.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // nullable = false
        String username = kakaoUserRequestDto.getUsername();                  // 카카오 아이디 (이메일)
        String password = UUID.randomUUID().toString();                 // 카카오 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = kakaoUserRequestDto.getNickname();                  // 카카오 닉네임

        // nullable = true
        String profileImage = kakaoUserRequestDto.getProfileImage();          // 카카오 프로필 이미지 (이미지 객체에 저장)
        String gender = kakaoUserRequestDto.getGender();                      // 카카오 성별

        String ageRange;

        System.out.println(kakaoUserRequestDto.getAgeRange());

        if (kakaoUserRequestDto.getAgeRange().equals("")) {
            ageRange = "";
        } else if (Integer.parseInt(kakaoUserRequestDto.getAgeRange().split("~")[0]) >= 50)
            ageRange = "50대 이상";
        else
            ageRange = kakaoUserRequestDto.getAgeRange().substring(0, 2).concat("대");  // 카카오 연령대

        // 가입 여부
        if (kakaoUser == null) {
            // 사용자 저장
            kakaoUser = User.builder()
                    .kakaoId(kakaoId)
                    .username(username)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .gender(gender)
                    .ageRange(ageRange)
                    .build();
            userRepository.save(kakaoUser);
            signStatus = false;                 // 처음 가입하면 false => 추가 정보 입력 페이지로 이동
        } else if (!kakaoUser.isStatus()) {     // 카카오 가입은 되었으나, 추가정보 입력 안했으면 false
            signStatus = false;
        } else {
            signStatus = true;                  // 이미 가입했으면 true => 메인 페이지로 이동
        }

        return kakaoUser;
    }

    // 4. 강제 로그인 처리
    private UserResponseDto forceLogin(User kakaoUser, HttpServletResponse response) {
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String token = JwtTokenUtils.generateJwtToken(userDetails);

        // Header 에 JWT 토큰 담아서 응답
        response.addHeader("Authorization", "Bearer " + token);

        // 추가정보입력 또는 가입한 사용자 (Location, Mbti NULL 값이 없어야함)
        String intro = null;
        String location = null;
        String locDetail = null;
        String longitude = null;
        String latitude = null;
        String mbti = null;
        List<String> interestListDtos = new ArrayList<>();
        if (signStatus) {
            intro = userDetails.getUser().getIntro();
            location = userDetails.getUser().getLocation().getLocation();
            locDetail = userDetails.getUser().getLocDetail().getLocDetail();
            longitude = userDetails.getUser().getLongitude();
            latitude = userDetails.getUser().getLatitude();
            mbti = userDetails.getUser().getMbti().getMbti();
            for (int i = 0; i < userDetails.getUser().getUserInterestList().size(); i++) {
                interestListDtos.add(userDetails.getUser().getUserInterestList().get(i).getInterest().getInterest());
            }
        }

        // Body 에 반환
        return UserResponseDto.builder()
                .username(userDetails.getUser().getUsername())
                .nickname(userDetails.getUser().getNickname())
                .gender(userDetails.getUser().getGender())
                .ageRange(userDetails.getUser().getAgeRange())
                .profileImage(userDetails.getUser().getProfileImage())
                .intro(intro)
                .location(location)
                .locDetail(locDetail)
                .longitude(longitude)
                .latitude(latitude)
                .mbti(mbti)
                .interestList(interestListDtos)
                .signStatus(signStatus)
                .build();
    }

    // 추가 정보 입력
    @Transactional
    public UserResponseDto updateProfile(User user,
                                         UserRequestDto userRequestDto,
                                         MultipartFile multipartFile
    ) throws IOException, ParseException {
        // 사용자 조회
        User findUser = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new NullPointerException("해당 사용자가 존재하지 않습니다.")
        );


        // 닉네임 필수값이므로, null 값이면 카카오 닉네임으로 설정
//        if (userRequestDto.getNickname() == null) {
//            userRequestDto.setNickname(user.getNickname());
//        }

        // 카카오 이미지 초기화
        String imgUrl = findUser.getProfileImage();
        // 이미지 첨부 있으면 URL 에 S3에 업로드된 파일 url 저장
        if (!multipartFile.isEmpty()) {
            imgUrl = s3Uploader.upload(multipartFile, imageDirName);
        }

        // 추가정보 설정하여 업데이트 (닉네임, 프로필, 소개글, 위치, 관심사, mbti)
        // 위치 조회
        Location location = locationRepository.findByLocation(userRequestDto.getLocation()).orElseThrow(
                () -> new IllegalArgumentException("해당 위치가 존재하지 않습니다.")
        );

        // 위치 조회
        LocDetail locDetail = locDetailRepository.findByLocDetailAndLocation(userRequestDto.getLocDetail(), location).orElseThrow(
                () -> new IllegalArgumentException("해당 상세 위치가 존재하지 않습니다.")
        );

        // mbti 조회
        Mbti mbti = mbtiRepository.findByMbti(userRequestDto.getMbti()).orElseThrow(
                () -> new IllegalArgumentException("해당 MBTI 가 존재하지 않습니다.")
        );

        // 반경
        Double latitude = Double.valueOf(userRequestDto.getLatitude());
        Double longitude = Double.valueOf(userRequestDto.getLongitude());
        String pointWKT = String.format("POINT(%s %s)", longitude, latitude);
        Point point = (Point) new WKTReader().read(pointWKT);

        findUser.update(userRequestDto, imgUrl, location, locDetail, mbti, point, true);

        // DB 저장
        userRepository.save(findUser);

        // 기존 관심사 삭제
        List<UserInterest> deleteUserInterest = userInterestRepository.findAllByUser(findUser);
        userInterestRepository.deleteAllInBatch(deleteUserInterest);

        // 관심사 리스트 조회
        List<UserInterest> userInterest = new ArrayList<>();
        for (int i = 0; i < userRequestDto.getInterestList().size(); i++) {
            Interest interest = interestRepository.findByInterest(userRequestDto.getInterestList().get(i)).orElseThrow(
                    () -> new IllegalArgumentException("해당 관심사가 존재하지 않습니다.")
            );

            userInterest.add(UserInterest.builder()
                    .user(findUser)
                    .interest(interest)
                    .build());
        }

        // DB 저장
        userInterestRepository.saveAll(userInterest);

        List<String> interestListDtos = new ArrayList<>();
        for (int i = 0; i < findUser.getUserInterestList().size(); i++) {
            interestListDtos.add(findUser.getUserInterestList().get(i).getInterest().getInterest());
        }

        // Body 에 반환
        return UserResponseDto.builder()
                .nickname(findUser.getNickname())
                .gender(findUser.getGender())
                .ageRange(findUser.getAgeRange())
                .profileImage(imgUrl)
                .intro(findUser.getIntro())
                .location(findUser.getLocation().getLocation())
                .locDetail(findUser.getLocDetail().getLocDetail())
                .locFull(findUser.getLocFull())
                .longitude(findUser.getLongitude())
                .latitude(findUser.getLatitude())
                .mbti(findUser.getMbti().getMbti())
                .interestList(interestListDtos)
                .username(findUser.getUsername())
                .signStatus(true)
                .build();
    }

    // 회원탈퇴
    @Transactional
    public void deleteProfile(User user) {
        // 사용자 조회
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 사용자가 존재하지 않습니다.")
        );
        // 사용자 관심사 리스트 조회
        List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
        // 사용자 게시글 리스트 조회
        List<Post> postList = postRepository.findAllByUser(findUser);
        List<Image> imageList = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();
        List<Likes> likesList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            // 게시글 이미지 리스트 조회
            imageList = imageRepository.findAllByPost(postList.get(i));
            // 게시글 댓글 리스트 조회
            commentList = commentRepository.findAllByPost(postList.get(i));
            // 게시글 좋아요 조회
            likesList = likesRepository.findAllByPost(postList.get(i));
        }

        // 채팅방 조회
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByGuestId(findUser.getId());

        // 초대받은 요청 삭제
        matchingRepository.deleteAllByGuestId(user.getId());
        // 초대 한 요청 삭제
        matchingRepository.deleteAllByHostId(user.getId());
        // 채팅방 삭제
        chatRoomRepository.deleteAll(chatRoomList);
        // 게시글 좋아요 삭제
        likesRepository.deleteAll(likesList);
        // 게시글 댓글 삭제
        commentRepository.deleteAll(commentList);
        // 게시글 이미지 삭제
        imageRepository.deleteAll(imageList);
        // 게시글 삭제
        postRepository.deleteAll(postList);
        // 사용자 관심사 삭제
        userInterestRepository.deleteAll(userInterestList);
        // 사용자 삭제
        userRepository.delete(findUser);
    }

    // 내가 쓴 글 조회
    @Transactional
    public List<PostResponseDto> getMyposts(Pageable pageable, User user) {

        // page, size, 내림차순으로 페이징한 내가 쓴 게시글 리스트
        List<Post> postList = postRepository.findAllByUser(user, pageable).getContent();
        // 반환할 게시글 리스트
        List<PostResponseDto> posts = new ArrayList<>();
        for (Post onePost : postList) {
            // 게시글 좋아요 수
            int likesCount = likesRepository.findAllByPost(onePost).size();
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
                        .mbti(oneComment.getUser().getMbti().getMbti())
                        .comment(oneComment.getComment())
                        .createdAt(oneComment.getCreatedAt())
                        .build());
            }

            // 내가 쓴 게시글 리스트
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
                    .imageList(images)
                    .commentList(comments)
                    .createdAt(onePost.getCreatedAt())
                    .build());
        }
        return posts;
    }

    public MbtiResponseDto viewProfile(User user) {
        // 해당 유저의 mbti와 동일한 mbti
        Mbti userMbti = user.getMbti();

        return MbtiResponseDto.builder()
                .name(userMbti.getMbti()) // 해당 MBTI 명칭
                .firstTitle(userMbti.getFirstTitle())
                .firstContent(userMbti.getFirstContent().replaceAll(System.getProperty("line.separator"), " "))
                .secondTitle(userMbti.getSecondTitle())
                .secondContent(userMbti.getSecondContent().replaceAll(System.getProperty("line.separator"), " "))
                .build();
    }
}