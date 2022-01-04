package com.sparta.mbti.service;

import com.sparta.mbti.dto.ChatMessageDto;
import com.sparta.mbti.dto.ChatMessageResponseDto;
import com.sparta.mbti.dto.ChatRoomDto;
import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.repository.ChatMessageRepository;
import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.util.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    // Redis repo
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    // 모든 채팅방 조회
    // 게스트로 있는 방도 조회
    public List<ChatRoomDto> findAllRoom(Long hostId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByHostIdOrGuestId(hostId, hostId);
        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();
        for(ChatRoom chatRoom : chatRoomList){
            chatRoomDtoList.add(ChatRoomDto.builder()
                    .guestId(chatRoom.getGuestId())
                    .roomId(chatRoom.getRoomId())
                    .hostId(hostId)
                    .build());
        }
        return chatRoomDtoList;
        //return opsHashChatRoom.values(CHAT_ROOMS);
    }

//    // 특정 채팅방 조회
//    public ChatRoom findRoomById(String roomId) {
//        //opsHashChatRoom.get(CHAT_ROOMS, id);
//        return chatRoomRepository.findByRoomId(roomId);
//    }

    //특정 채팅방의 모든 채팅 조회
    public ChatMessageResponseDto readAllMessage(String roomId){
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomId(roomId);
        List<ChatMessageDto> chatMessageDtoList = new ArrayList<>();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        for (ChatMessage message : chatMessageList) {
            chatMessageDtoList.add(ChatMessageDto.builder()
                    .type(message.getType())
                    .message(message.getMessage())
                    .roomId(message.getRoomId())
                    .sender(message.getSender())
                    .build());
        }
        return ChatMessageResponseDto.builder()
                .guestId(chatRoom.getGuestId())
                .hostId(chatRoom.getHostId())
                .roomId(roomId)
                .chatMessageDtoList(chatMessageDtoList)
                .build();
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    public ChatRoom createChatRoom(Long guestId, Long hostId) {
        ChatRoom chatRoom = new ChatRoom(guestId, hostId);

        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);

        chatRoomRepository.save(chatRoom);

        return chatRoom;
    }
}
