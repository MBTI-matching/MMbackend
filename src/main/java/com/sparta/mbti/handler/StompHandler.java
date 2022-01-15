package com.sparta.mbti.handler;

import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.jwt.JwtDecoder;
import com.sparta.mbti.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증

        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtTokenUtils.validateToken(accessor.getFirstNativeHeader("token"));
        }
        else if(StompCommand.SUBSCRIBE == accessor.getCommand()){
            //String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

            System.out.println(message.getHeaders());
            String jwtToken = accessor.getFirstNativeHeader("token");
            String username = jwtDecoder.decodeUsername(jwtToken);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new NullPointerException("존재하지 않는 사용자 입니다."));

            Long userId = user.getId();
            //chatRoomService.setUserEnterInfo(sessionId, roomId,userId);
        }
        return message;
    }
}