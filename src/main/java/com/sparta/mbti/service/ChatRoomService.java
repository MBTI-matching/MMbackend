package com.sparta.mbti.service;

import com.sparta.mbti.dto.ChatMessageResponseDto;
import com.sparta.mbti.dto.ChatRoomRequestDto;
import com.sparta.mbti.dto.ChatRoomResponseDto;
import com.sparta.mbti.model.ChatMessage;
import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.ChatMessageRepository;
import com.sparta.mbti.repository.ChatRoomRepository;
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
        //사용자가 초대했을 때 채팅방 리스트
        List<ChatRoom> chatRoomHostList = chatRoomRepository.findAllByHostId(hostId);
        //사용자가 초대받았을 때 채팅방 리스트
        List<ChatRoom> chatRoomGuestList = chatRoomRepository.findAllByGuestId(hostId);

        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        for(ChatRoom chatRoom : chatRoomHostList){
            chatRoomResponseDtoList.add(ChatRoomResponseDto.builder()
                    .guestId(chatRoom.getGuestId())
                    .roomId(chatRoom.getRoomId())
                    .guestImg(chatRoom.getGuestImg())
                    .guestMbti(chatRoom.getGuestMbti())
                    .guestNick(chatRoom.getGuestNick())
                    .build());
        }
        for(ChatRoom chatRoom : chatRoomGuestList){
            User host = userRepository.findById(chatRoom.getHostId()).orElse(null);
            chatRoomResponseDtoList.add(ChatRoomResponseDto.builder()
                    .guestId(chatRoom.getHostId())
                    .roomId(chatRoom.getRoomId())
                    .guestImg(host.getProfileImage())
                    .guestMbti(host.getMbti().getMbti())
                    .guestNick(host.getNickname())
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

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다. -> redis hash는 보류
    public ChatRoom createChatRoom(Long hostId, ChatRoomRequestDto chatRoomRequestDto) {

        User guest = userRepository.findByUsername(chatRoomRequestDto.getGuestEmail()).orElse(null);

        // roomId: entity에서 작성하는 것보단 service에서 만들고 entity에서는 연결만 하는 게 더 좋아보임. 밀착 참고.
        String roomId = UUID.randomUUID().toString();

        ChatRoom chatRoom = new ChatRoom(
                hostId,
                guest.getId(),
                chatRoomRequestDto.getGuestImg(),
                chatRoomRequestDto.getGuestMbti(),
                chatRoomRequestDto.getGuestNick(),
                roomId
        );

        //opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);

        chatRoomRepository.save(chatRoom);

        return chatRoom;
    }
}
