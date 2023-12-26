package com.core.market.chat.domain.repository;

import com.core.market.chat.domain.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CurrentChatRoomInfoRepository extends CrudRepository<CurrentChatRoomInfo, Long> {

    List<CurrentChatRoomInfo> findByRoomId(Long roomId);

    Optional<CurrentChatRoomInfo> findByRoomIdAndEmail(Long roomId, String email);
}
