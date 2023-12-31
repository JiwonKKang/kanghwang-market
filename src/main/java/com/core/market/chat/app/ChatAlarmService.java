package com.core.market.chat.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.user.domain.Member;
import com.core.market.user.domain.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatAlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String EVENT_NAME = "alarm";
    private final EmitterRepository emitterRepository;

    public void send(Member receiver) {
        emitterRepository.getEmitter(receiver.getId()).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(receiver.getId().toString()).name(EVENT_NAME).data("new unread chat"));
            } catch (IOException e) {
                emitterRepository.delete(receiver.getId());
                throw new CustomException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter found"));
    }

    public SseEmitter connectAlarm(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));
        try {
            sseEmitter.send(SseEmitter.event().id("").name(EVENT_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

}
