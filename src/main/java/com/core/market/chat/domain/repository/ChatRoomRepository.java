package com.core.market.chat.domain.repository;

import com.core.market.chat.domain.ChatRoom;
import com.core.market.trade.domain.TradePost;
import com.core.market.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findChatRoomByBuyerAndSellerAndPost(Member buyer, Member seller, TradePost post);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.buyer.id = :memberId OR cr.seller.id = :memberId")
    List<ChatRoom> findChatRoomsByMemberId(@Param("memberId") Long memberId);

}
