package com.sparta.mbti.repository;

import com.sparta.mbti.model.LocDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocDetailRepository extends JpaRepository<LocDetail, Long> {
    Optional<LocDetail> findByLocDetail(String locDetail);
}
