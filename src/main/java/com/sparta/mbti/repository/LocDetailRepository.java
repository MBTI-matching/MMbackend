package com.sparta.mbti.repository;

import com.sparta.mbti.model.LocDetail;
import com.sparta.mbti.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocDetailRepository extends JpaRepository<LocDetail, Long> {
    Optional<LocDetail> findByLocDetailAndLocation(String locDetail, Location location);
}
