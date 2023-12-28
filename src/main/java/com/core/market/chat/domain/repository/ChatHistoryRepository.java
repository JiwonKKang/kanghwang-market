package com.core.market.chat.domain.repository;

import com.core.market.chat.domain.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    @Query(value = """
            select COUNT(*) from ChatHistory ch
            where ch.chatRoom.id = :roomId
            and ch.sender.id != :memberId
            and ch.unreadCount = 1
            order by ch.createdAt desc
            """)
    int findUnreadChatHistoriesCountInChatRoom(@Param("memberId") Long memberId, @Param("roomId") Long roomId);

    @Query(value = """
            select COUNT(*) from ChatHistory ch
            join ChatRoom cr on ch.chatRoom.id = cr.id
            where cr.buyer.id = :memberId or cr.seller.id = :memberId
            """)
    int getTotalUnreadChatCount(@Param("memberId") Long memberId);

    @Query(value = "select ChatHistory from ChatHistory ch where ch.chatRoom.id = :roomId order by ch.createdAt desc limit 1")
    ChatHistory findLastChatHistory(@Param("roomId") Long roomId);


}
