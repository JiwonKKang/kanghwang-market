package com.core.market.user.domain.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class EmitterRepository {

    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(Long userId, SseEmitter emitter) {
        String key = getKey(userId);
        log.info("save Emitter {} : {}", key, emitter);

        return emitterMap.put(key, emitter);
    }

    public Optional<SseEmitter> getEmitter(Long userId) {
        String key = getKey(userId);
        SseEmitter sseEmitter = emitterMap.get(key);
        log.info("get Emitter {} : {}", key, sseEmitter);
        return Optional.ofNullable(sseEmitter);
    }

    public void delete(Long userId) {
        emitterMap.remove(getKey(userId));
        log.info("delete Emitter {}", getKey(userId));
    }

    private String getKey(Long userId) {
        return "emitter:UID - " + userId;
    }

}

