package com.core.market.common.security.handler;

import com.core.market.chat.domain.ChatRoom;
import com.core.market.chat.domain.repository.ChatRoomRepository;
import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선 순위를 높게 설정해서 SecurityFilter들 보다 앞서 실행되게 해준다.
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final ChatRoomRepository chatRoomRepository;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        switch (command) {
            case CONNECT -> {
                String email = validateToken(accessor);
                connectChatRoom(accessor, email);
            }
            case SEND -> {
                validateToken(accessor);
            }

        }
        return message;
    }

    private String validateToken(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("token");
        log.info("웹소켓 연결 토큰 검증 시작 - {}", token);
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            log.info("웹소켓 토큰 검증 오류 발생");
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        jwtTokenUtil.validateToken(token.replace(TOKEN_PREFIX, ""));
        log.info("웹소켓 토큰 검증 성공");

        return jwtTokenUtil.extractEmail(token.replace(TOKEN_PREFIX, ""));
    }

    private void connectChatRoom(StompHeaderAccessor accessor, String email) {
        String StringRoomId = accessor.getFirstNativeHeader("roomId");

        if (StringRoomId == null || StringRoomId.isEmpty()) {
            log.info("웹소켓 요청 헤더 채팅방 번호 없음");
            throw new CustomException(ErrorCode.INVALID_ROOM_ID);
        }

        Long roomId = Long.valueOf(StringRoomId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (chatRoom.getSeller().getEmail().equals(email)) {
            chatRoom.sellerIn();
        } else {
            chatRoom.buyerIn();
        }
        chatRoomRepository.save(chatRoom);
    }
}
