package com.sparta.mbti.repository;

import com.sparta.mbti.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
