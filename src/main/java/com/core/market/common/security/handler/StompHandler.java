package com.core.market.common.security.handler;

import com.core.market.chat.app.ChatRoomService;
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
    private final ChatRoomService chatRoomService;

    private static final String TOKEN_PREFIX = "Bearer ";


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String token = accessor.getFirstNativeHeader("token");
            log.info("웹소켓 연결 토큰 검증 시작 - {}", token);
            if (token == null || !token.startsWith(TOKEN_PREFIX)) {
                throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
            }
            log.info("웹소켓 토큰 검증 성공 - {}", message);
            jwtTokenUtil.validateToken(token.replace(TOKEN_PREFIX, ""));
        }
        return message;
    }
}
