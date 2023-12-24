package com.core.market.chat.domain.repository;

import com.core.market.chat.domain.ChatRoom;
import com.core.market.trade.domain.TradePost;
import com.core.market.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findChatRoomByBuyerAndSellerAndPost(Member buyer, Member seller, TradePost post);

}
