package com.core.market.chat.api.request;

import java.time.LocalDateTime;

public record ChatMessage(
        Long roomId,
        String message,
        Long senderId,
        LocalDateTime createdAt
) {

}
