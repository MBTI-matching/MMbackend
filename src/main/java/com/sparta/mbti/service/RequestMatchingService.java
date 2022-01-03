package com.sparta.mbti.service;

import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestMatchingService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
}
