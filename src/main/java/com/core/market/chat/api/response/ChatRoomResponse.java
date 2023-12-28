package com.core.market.chat.api.response;

import com.core.market.chat.domain.ChatRoom;
import com.core.market.user.api.response.UserResponse;
import com.core.market.user.domain.Member;

public record ChatRoomResponse(
        Long roomId,
        UserResponse chatPartner,
        ChatHistoryResponse lastChatHistory,
        int totalUnreadCount
) {

    public static ChatRoomResponse of(Long chatRoomId, ChatHistoryResponse lastChatHistory, Member chatPartner, int totalUnreadCount) {
        return new ChatRoomResponse(
                chatRoomId,
                UserResponse.from(chatPartner),
                lastChatHistory,
                totalUnreadCount
        );
    }

}
