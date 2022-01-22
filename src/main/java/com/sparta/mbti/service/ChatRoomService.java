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
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

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
        //사용자가 초대했을 때 채팅방 리스트
        List<ChatRoom> chatRoomHostList = null;
        try {
            chatRoomHostList = chatRoomRepository.findAllByHostId(hostId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //사용자가 초대받았을 때 채팅방 리스트
        List<ChatRoom> chatRoomGuestList = null;
        try {
            chatRoomGuestList = chatRoomRepository.findAllByGuestId(hostId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        if (chatRoomHostList != null)
            for (ChatRoom chatRoom : chatRoomHostList) {
                ChatMessage msg = new ChatMessage();
                if (chatMessageRepository.existsByRoomId(chatRoom.getRoomId()))
                    msg = chatMessageRepository.findFirstByRoomIdOrderByIdDesc(chatRoom.getRoomId());
                chatRoomResponseDtoList.add(ChatRoomResponseDto.builder()
                        .guestId(chatRoom.getGuestId())
                        .roomId(chatRoom.getRoomId())
                        .guestImg(chatRoom.getGuestImg())
                        .guestMbti(chatRoom.getGuestMbti())
                        .guestNick(chatRoom.getGuestNick())
                        .lastMessage(msg != null ? msg.getMessage() : "")
                        .messageType(msg != null ? msg.getType() : null)
                        .messageTime(msg != null ? msg.getDate() : null)
                        .build());
            }
        //초대 받은 방의 메세지 리스트
        //유저 탈퇴 시 예오
        if (chatRoomGuestList != null)
            for (ChatRoom chatRoom : chatRoomGuestList) {
                User host = userRepository.findById(chatRoom.getHostId()).orElse(null);
                ChatMessage msg = new ChatMessage();
                if (chatMessageRepository.existsByRoomId(chatRoom.getRoomId()))
                    msg = chatMessageRepository.findFirstByRoomIdOrderByIdDesc(chatRoom.getRoomId());

                chatRoomResponseDtoList.add(ChatRoomResponseDto.builder()
                        .guestId(chatRoom.getHostId())
                        .roomId(chatRoom.getRoomId())
                        .guestImg(host != null ? host.getProfileImage() : null)
                        .guestMbti(host != null ? host.getMbti().getMbti() : "ENTJ")
                        .guestNick(host != null ? host.getNickname() : "없는 사용자 입니다.")
                        .lastMessage(msg != null ? msg.getMessage() : "")
                        .messageType(msg != null ? msg.getType() : null)
                        .messageTime(msg != null ? msg.getDate() : null)
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
    public List<ChatMessageResponseDto> readAllMessage(Pageable pageable, String roomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomIdOrderByIdDesc(pageable, roomId);
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoomId(roomId);

        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();


        for (int i = chatMessageList.toArray().length - 1; 0 <= i; i--) {
            chatMessageResponseDtoList.add(ChatMessageResponseDto.builder()
                    .type(chatMessageList.get(i).getType())
                    .message(chatMessageList.get(i).getMessage())
                    .senderId(chatMessageList.get(i).getSenderId())
                    .senderName(chatMessageList.get(i).getSenderName())
                    .senderImg(chatMessageList.get(i).getSenderImg())
                    .senderNick(chatMessageList.get(i).getSenderNick())
                    .date(chatMessageList.get(i).getDate())
                    .totalMessage(chatMessages.size())
                    .build());
        }

        return chatMessageResponseDtoList;
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다. -> redis hash는 보류
    public ChatRoom createChatRoom(Long hostId, ChatRoomRequestDto chatRoomDto) {

        User guest = userRepository.findByUsername(chatRoomDto.getGuestEmail())
                .orElseThrow(() -> new NullPointerException("존재하지 않는 유저입니다."));

        String roomId = UUID.randomUUID().toString();

        ChatRoom chatRoom = ChatRoom.builder().roomId(roomId)
                .guestId(guest.getId())
                .guestMbti(chatRoomDto.getGuestMbti())
                .guestImg(chatRoomDto.getGuestImg())
                .roomId(roomId)
                .guestNick(chatRoomDto.getGuestNick())
                .hostId(hostId)
                .build();

        //opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);

        chatRoomRepository.save(chatRoom);
        matchingService.deleteMatching(guest, hostId);
        return chatRoom;
    }

    // 채팅방 나가기(유저가 호스트일때, 게스트일 때 나눠서)
    public void exitChatRoom(Long userId, String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 채팅방입니다."));
        if (room.getGuestId().equals(userId)) {
            room.deleteGuestId();
            chatRoomRepository.save(room);
        } else if (room.getHostId().equals(userId)) {
            room.deleteHostId();
            chatRoomRepository.save(room);
        }
    }

    public void deleteChatRoom(Long userId, String roomId){
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomId(roomId);
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 채팅방입니다."));
        if (room.getGuestId().equals(userId)) {
            room.deleteGuestId();
            chatRoomRepository.delete(room);
            chatMessageRepository.deleteAll(chatMessageList);
        } else if (room.getHostId().equals(userId)) {
            room.deleteHostId();
            chatMessageRepository.deleteAll(chatMessageList);
            chatRoomRepository.delete(room);
        }
    }
}
