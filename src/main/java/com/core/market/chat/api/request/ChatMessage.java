package com.core.market.chat.api.request;

import com.core.market.chat.domain.ChatHistory;
import com.core.market.chat.domain.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessage(
        MessageType messageType,
        Long roomId,
        String message,
        Long senderId,
        Integer unreadCount,
        LocalDateTime createdAt
) {
    public static ChatMessage of(MessageType messageType, ChatHistory chatHistory) {
        return new ChatMessage(
                messageType,
                chatHistory.getChatRoom().getId(),
                chatHistory.getMessage(),
                chatHistory.getSender().getId(),
                chatHistory.getUnreadCount(),
                chatHistory.getCreatedAt()
        );
    }
}
