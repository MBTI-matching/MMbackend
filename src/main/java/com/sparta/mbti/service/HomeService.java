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

@RequiredArgsConstructor
@Service
public class HomeService {
    private final UserRepository userRepository;
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
    public List<PostResponseDto> getAllposts(Pageable pageable) {
        // page, size, 내림차순으로 페이징한 게시글 리스트
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        // 반환할 게시글 리스트
        List<PostResponseDto> posts = new ArrayList<>();
        for (Post onePost : postList) {
            // 게시글 좋아요 수
            int likesCount = (int)likesRepository.findAllByPost(onePost).size();
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
                                .imageList(images)
                                .commentList(comments)
                                .createdAt(onePost.getCreatedAt())
                                .build());
        }
        return posts;
    }
}
