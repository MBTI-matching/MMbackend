package com.sparta.mbti.repository;

import com.sparta.mbti.model.Location;
import com.sparta.mbti.model.Mbti;
import com.sparta.mbti.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
    List<User> findAllByLocation(Location location);
    List<User> findAllByLocationAndMbtiIn(Location location, List<Mbti> mbtiList);
    List<User> findAllByLocationAndMbti(Location location, Mbti mbti);
}
