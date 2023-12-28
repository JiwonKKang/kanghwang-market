package com.core.market.chat.api.response;

import com.core.market.chat.domain.ChatHistory;

import java.time.LocalDateTime;

public record ChatHistoryResponse(
        Long senderId,
        String message,
        int unreadCount,
        LocalDateTime createdAt
) {

    public static ChatHistoryResponse of(ChatHistory chatHistory) {
        return new ChatHistoryResponse(
                chatHistory.getSender().getId(),
                chatHistory.getMessage(),
                chatHistory.getUnreadCount(),
                chatHistory.getCreatedAt()
        );
    }
}
