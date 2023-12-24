package com.core.market.chat.domain.repository;

import com.core.market.chat.domain.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
}
