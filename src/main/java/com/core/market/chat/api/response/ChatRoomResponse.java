package com.core.market.chat.api.response;

import com.core.market.chat.domain.ChatRoom;

public record ChatRoomResponse(
        Long roomId
) {

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId()
        );
    }

}
