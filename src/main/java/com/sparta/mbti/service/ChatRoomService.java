package com.sparta.mbti.service;

import com.sparta.mbti.dto.ChatMessageResponseDto;
import com.sparta.mbti.dto.ChatRoomRequestDto;
import com.sparta.mbti.dto.ChatRoomResponseDto;
import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatMessageRepository;
import com.sparta.mbti.repository.ChatRoomRepository;
import com.sparta.mbti.repository.MatchingRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final MatchingRepository matchingRepository;
    private final MatchingService matchingService;

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
    public List<ChatRoomResponseDto> findAllRoom(Long hostId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByHostIdOrGuestId(hostId, hostId);

        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        for(ChatRoom chatRoom : chatRoomList){
            chatRoomResponseDtoList.add(ChatRoomResponseDto.builder()
                    .guestId(chatRoom.getGuestId())
                    .roomId(chatRoom.getRoomId())
                    .guestImg(chatRoom.getGuestImg())
                    .guestMbti(chatRoom.getGuestMbti())
                    .guestNick(chatRoom.getGuestNick())
                    .build());
        }
        return chatRoomResponseDtoList;
        //return opsHashChatRoom.values(CHAT_ROOMS);
    }

//    // 특정 채팅방 조회
//    public ChatRoom findRoomById(String roomId) {
//        //opsHashChatRoom.get(CHAT_ROOMS, id);
//        return chatRoomRepository.findByRoomId(roomId);
//    }

    //특정 채팅방의 모든 채팅 조회
    public List<ChatMessageResponseDto> readAllMessage(String roomId){
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomId(roomId);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();

        for (ChatMessage message : chatMessageList) {
            chatMessageResponseDtoList.add(ChatMessageResponseDto.builder()
                    .type(message.getType())
                    .message(message.getMessage())
                    .senderId(message.getSenderId())
                    .senderName(message.getSenderName())
                    .senderImg(message.getSenderImg())
                    .senderNick(message.getSenderNick())
                    .date(message.getDate())
                    .build());
        }
        return chatMessageResponseDtoList;
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    public ChatRoom createChatRoom(Long hostId, ChatRoomRequestDto chatRoomRequestDto) {
        User guest = userRepository.findByUsername(chatRoomRequestDto.getGuestId()).orElse(null);
        ChatRoom chatRoom = new ChatRoom(hostId, guest.getId(), chatRoomRequestDto.getGuestImg(), chatRoomRequestDto.getGuestMbti(), chatRoomRequestDto.getGuestNick());

        //opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);

        chatRoomRepository.save(chatRoom);
        matchingService.deleteMatching(guest, hostId);
        return chatRoom;
    }
}
