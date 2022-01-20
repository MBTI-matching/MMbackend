package com.sparta.mbti.repository;

import com.sparta.mbti.model.LocDetail;
import com.sparta.mbti.model.Location;
import com.sparta.mbti.model.Mbti;
import com.sparta.mbti.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByUsername(String username);
    List<User> findAllByRole(String role);
    List<User> findAllByLocationAndLocDetail(Location location, LocDetail locDetail);
    List<User> findAllByLocationAndLocDetailAndMbtiIn(Location location, LocDetail locDetail, List<Mbti> mbtiList);
    List<User> findAllByLocationAndLocDetailAndMbti(Location location, LocDetail locDetail, Mbti mbti);

}
