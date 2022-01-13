package com.sparta.mbti.service;

import com.sparta.mbti.dto.request.UserRequestDto;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.*;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.utils.S3Uploader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class) // @Mock 사용하기 위해 Mockito 테스트 실행을 확장
class UserServiceTest {
    @InjectMocks            // @Mock 붙은 객체를 @InjectMocks 객체에 주입
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private S3Uploader s3Uploader;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private MbtiRepository mbtiRepository;
    @Mock
    private InterestRepository interestRepository;
    @Mock
    private UserInterestRepository userInterestRepository;

    private UserDetailsImpl userDetailsNull;
    private UserDetailsImpl userDetails01;
    private UserDetailsImpl userDetails02;
    private User user01;
    private User user02;
    private Location location01;
    private Location location02;
    private Mbti mbti01;
    private Mbti mbti02;
    private Interest interest01;
    private Interest interest02;
    private UserInterest userInterest01;
    private UserInterest userInterest02;
    private UserRequestDto userRequestDto01;
    private MultipartFile multipartFile01;

    @BeforeEach
        // 테스트 실행 이전에 수행
    void setUp() throws IOException {
        // 사용자 Null
        userDetailsNull = null;

        // 사용자 존재 user01
        user01 = User.builder()
                .id(100L)
                .kakaoId(100L)
                .username("test100@naver.com")
                .password("password")
                .nickname("손흥민")
                .profileImage("https://gorokke.shop/image/profileDefaultImg.jpg")
                .gender("male")
                .ageRange("30대")
                .status(false)
                .build();
        userDetails01 = new UserDetailsImpl(user01);

        // 사용자 존재 user02
        user02 = User.builder()
                .id(200L)
                .kakaoId(200L)
                .username("test200@naver.com")
                .password("password")
                .nickname("이민아")
                .profileImage("https://gorokke.shop/image/profileDefaultImg.jpg")
                .gender("female")
                .ageRange("20대")
                .status(false)
                .build();
        userDetails02 = new UserDetailsImpl(user02);

        // 추가정보 입력 요청 dto
        userRequestDto01 = UserRequestDto.builder()
                .nickname("토트넘골잡이")
                .profileImage("https://gorokke.shop/image/profileDefaultImg.jpg")
                .intro("대표 골잡이 손흥민입니다.")
                .location("중구")
                .mbti("INTJ")
                .build();

        // 프로필 이미지
        multipartFile01 = new MockMultipartFile(
                "multipartFile",
                "TIL.png",
                "multipart/form-data",
                new FileInputStream("/Users/gimjong-ug/Desktop/TIL.png"));
    }

    @Nested
    @DisplayName("추가정보 입력_실패 케이스")
    class UpdateUserInfo_Fail {
        @Test
        @DisplayName("사용자 존재하지 않을 떄")
        public void updateProfile_fail01() {
            // when, then
            assertThrows(NullPointerException.class,
                    () -> userService.updateProfile(userDetailsNull.getUser(), userRequestDto01, multipartFile01),
                    "해당 사용자가 존재하지 않습니다.");
        }

//        @Test
//        @DisplayName("지원하지 않는 파일")
//        @Disabled
//        public void updateProfile_fail02() throws FileNotFoundException {
//            // given
////            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));
//
//            // mocking
//            when(userRepository.findById(user01.getId())).thenReturn(Optional.of(user01));
//
//            // when
//            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            });
//
//            // then
//            assertEquals("파일 업로드에 실패하였습니다.", exception.getMessage());
//        }
    }
}