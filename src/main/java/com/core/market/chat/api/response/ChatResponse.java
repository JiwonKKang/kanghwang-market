package com.core.market.chat.api.response;

import com.core.market.chat.domain.ChatHistory;
import com.core.market.chat.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;

public record ChatResponse(

        List<ChatHistoryResponse> chatHistories
) {
    public record ChatHistoryResponse(
            Long senderId,
            Boolean isSender,
            String message,
            LocalDateTime createdAt
    ) {

        public static ChatHistoryResponse of(ChatHistory chatHistory, Long senderId) {
            return new ChatHistoryResponse(
                    chatHistory.getId(),
                    chatHistory.getId().equals(senderId),
                    chatHistory.getMessage(),
                    chatHistory.getCreatedAt()
            );
        }
    }

    public static ChatResponse of(ChatRoom chatRoom, Long senderId) {
        return new ChatResponse(
                chatRoom.getChatHistories().stream()
                        .map(chatHistory -> ChatHistoryResponse.of(chatHistory, senderId))
                        .toList()
        );
    }
}

