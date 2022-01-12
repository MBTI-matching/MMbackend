package com.sparta.mbti.service;

import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.*;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.utils.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // @Mock 사용하기 위해 Mockito 테스트 실행을 확장
class PostServiceTest {
    @InjectMocks            // @Mock 붙은 객체를 @InjectMocks 객체에 주입
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private S3Uploader s3Uploader;
    @Mock
    private LikesRepository likesRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
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
    private Post post01;
    private Post post02;
    private Image image01;
    private Image image02;
    private PostRequestDto postRequestDto01;
    private List<MultipartFile> multipartFileList01 = new ArrayList<>();

    @BeforeEach
        // 테스트 실행 이전에 수행
    void setUp() throws IOException {
        // 사용자 Null
        userDetailsNull = null;
        // 사용자 존재 user01
        location01 = Location.builder()
                .id(1L)
                .location("종로구")
                .longitude("37.59708565")
                .latitude("126.9706197")
                .build();
        mbti01 = Mbti.builder()
                .id(1L)
                .mbti("ENTJ")
                .build();
        interest01 = Interest.builder()
                .id(1L)
                .interest("운동")
                .build();
        user01 = User.builder()
                .id(100L)
                .kakaoId(100L)
                .username("test100@naver.com")
                .password("password")
                .nickname("손흥민")
                .profileImage("https://gorokke.shop/image/profileDefaultImg.jpg")
                .gender("male")
                .ageRange("30대")
                .intro("안녕하세요~!")
                .location(location01)
                .mbti(mbti01)
                .status(true)
                .build();
        userInterest01 = UserInterest.builder()
                .id(100L)
                .user(user01)
                .interest(interest01)
                .build();
        userDetails01 = new UserDetailsImpl(user01);
        // 사용자 user01 의 post01
        post01 = Post.builder()
                .id(1L)
                .content("축구 차러 가실분~~!")
                .tag("운동")
                .user(userDetails01.getUser())
                .build();
        image01 = Image.builder()
                .imageLink("https://dimg.donga.com/wps/NEWS/IMAGE/2021/04/12/106357558.1.jpg")
                .post(post01)
                .build();

        // 사용자 존재 user02
        location02 = Location.builder()
                .id(1L)
                .location("종로구")
                .longitude("37.59708565")
                .latitude("126.9706197")
                .build();
        mbti02 = Mbti.builder()
                .id(1L)
                .mbti("ENTJ")
                .build();
        interest02 = Interest.builder()
                .id(1L)
                .interest("운동")
                .build();
        user02 = User.builder()
                .id(200L)
                .kakaoId(200L)
                .username("test200@naver.com")
                .password("password")
                .nickname("이민아")
                .profileImage("https://gorokke.shop/image/profileDefaultImg.jpg")
                .gender("female")
                .ageRange("20대")
                .intro("ㅎㅇㄹ~!")
                .location(location02)
                .mbti(mbti02)
                .status(true)
                .build();
        userInterest02 = UserInterest.builder()
                .id(200L)
                .user(user02)
                .interest(interest02)
                .build();
        userDetails02 = new UserDetailsImpl(user02);
        // 사용자 user01 의 post01
        post02 = Post.builder()
                .id(2L)
                .content("헬스 하러 가실분~~!")
                .tag("운동")
                .user(userDetails02.getUser())
                .build();
        image02 = Image.builder()
                .imageLink("https://dimg.donga.com/wps/NEWS/IMAGE/2021/04/12/106357558.1.jpg")
                .post(post02)
                .build();

        // 게시글 요청 dto
        postRequestDto01 = PostRequestDto.builder()
                .content("재테크 공부 하실분~~!")
                .tag("재테크")
                .build();

        // 게시글 이미지 리스트
        MultipartFile multipartFile = new MockMultipartFile(
                "multipartFile",
                "TIL.png",
                "multipart/form-data",
                new FileInputStream("/Users/gimjong-ug/Desktop/TIL.png"));
        multipartFileList01.add(multipartFile);
    }

    @Test
    @DisplayName("게시글 작성_사용자 NULL 일 때")
    public void createPostTest01() {
        // when, then
        assertThrows(NullPointerException.class,
                () -> postService.createPost(userDetailsNull.getUser(), postRequestDto01, multipartFileList01),
                "로그인하지 않았습니다.");
    }

    @Test
    @DisplayName("게시글 작성_이미지가 NULL 일 때")
    public void createPostTest02() throws IOException {
        // given
        List<MultipartFile> multipartFileList02 = new ArrayList<>();
        // when
        postService.createPost(userDetails01.getUser(), postRequestDto01, multipartFileList02);

    }

    @Test
    @DisplayName("게시글 작성")
    public void createPostTest03() throws IOException {
        // when
        postService.createPost(userDetails01.getUser(), postRequestDto01, multipartFileList01);
    }

    @Test
    @DisplayName("게시글 상세조회_postId가 없을 때")
    public void getPostDetail01() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(null);

        // when, then
        assertThrows(NullPointerException.class,
                () -> postService.deletePost(postId, userDetails01.getUser()),
                "해당 게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 상세조회")
    public void getPostDetail02() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post01));

        // when
        PostResponseDto postResponseDto = postService.detailPost(postId, userDetails01.getUser());

        // then
        assertThat(postResponseDto.getPostId()).isEqualTo(post01.getId());
        assertThat(postResponseDto.getContent()).isEqualTo(post01.getContent());
        assertThat(postResponseDto.getNickname()).isEqualTo(post01.getUser().getNickname());
    }

    @Test
    @DisplayName("게시글 수정_postId가 없을 때")
    public void updatePost01() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(null);

        // when, then
        assertThrows(NullPointerException.class,
                () -> postService.updatePost(postId, userDetails01.getUser(), postRequestDto01),
                "해당 게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 수정_작성자가 아닐 때")
    public void updatePost02() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post01));

        // when, then
        assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(postId, userDetails02.getUser(), postRequestDto01),
                "해당 게시글의 작성자만 수정 가능합니다.");
    }

    @Test
    @DisplayName("게시글 수정")
    public void updatePost03() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post01));

        // when
        postService.updatePost(postId, userDetails01.getUser(), postRequestDto01);
    }

    @Test
    @DisplayName("게시글 삭제_postId가 없을 때")
    public void deletePost01() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(null);

        // when, then
        assertThrows(NullPointerException.class,
                () -> postService.deletePost(postId, userDetails01.getUser()),
                "해당 게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 삭제_작성자가 아닐 때")
    public void deletePost02() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post01));

        // when, then
        assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(postId, userDetails02.getUser()),
                "해당 게시글의 작성자만 삭제 가능합니다.");
    }

    @Test
    @DisplayName("게시글 삭제")
    public void deletePost03() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post01));

        // when
        postService.deletePost(postId, userDetails01.getUser());
    }

    @Test
    @DisplayName("게시글 좋아요_postId가 없을 때")
    public void likePost01() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(null);

        // when, then
        assertThrows(NullPointerException.class,
                () -> postService.likesOnOff(postId, userDetails01.getUser()),
                "해당 게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 좋아요")
    public void likePost02() {
        // given
        Long postId = post01.getId();

        // mocking
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post01));

        // when
        postService.likesOnOff(postId, userDetails01.getUser());
    }

    @Test
    @DisplayName("관심사별 게시글_interestId가 없을 때")
    public void getIntPost01() {
        // given
        Long interestId = interest01.getId();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        // mocking
        Mockito.lenient().when(interestRepository.findById(interestId)).thenReturn(null);

        // when, then
        assertThrows(NullPointerException.class,
                () -> postService.getIntPosts(null, pageable, userDetails01.getUser()),
                "해당 관심사는 다루지 않습니다.");
    }

//    @Test
//    @DisplayName("관심사별 게시글")
//    public void getIntPost02() throws IOException {
//        // given
//        Long interestId = interest01.getId();
//        Pageable pageable = PageRequest.of(0, 500, Sort.by("createdAt").descending());
//        // mocking
////        when(interestRepository.findById(interestId)).thenReturn(Optional.ofNullable(interest01));
//
//        // when
//        postService.getIntPosts(interestId, pageable, userDetails01.getUser());
//    }
}