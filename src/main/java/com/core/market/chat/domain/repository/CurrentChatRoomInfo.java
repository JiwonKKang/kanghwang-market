package com.core.market.chat.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "chatRoom", timeToLive = 1209600)
public class CurrentChatRoomInfo {

    @Id
    private Long roomId;

    @Indexed
    private String email;

}
