package com.sparta.mbti.service;

import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    //private Map<String, ChatRoom> chatRoomMap;
    private final ChatRepository chatRepository;

//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }

    public List<ChatRoom> findAllRoom() {
        // 채팅방 생성순서 최근 순으로 반환
        List<ChatRoom> chatRooms;
        chatRooms = chatRepository.findAll();
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    public ChatRoom findRoomById(String id) {
        return chatRepository.findByRoomId(id);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .name(name)
                .build();
        chatRepository.save(chatRoom);
        //chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }
}