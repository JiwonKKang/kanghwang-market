package com.core.market.chat.api.request;

import java.time.LocalDateTime;

public record ChatMessage(
        Long roomId,
        String message,
        Long senderId,
        Integer unreadCount,
        LocalDateTime createdAt
) {
    public static ChatMessage of(ChatMessage sendMessage, Integer unreadCount) {
        return new ChatMessage(
                sendMessage.roomId,
                sendMessage.message(),
                sendMessage.senderId,
                unreadCount,
                sendMessage.createdAt
        );
    }
}
