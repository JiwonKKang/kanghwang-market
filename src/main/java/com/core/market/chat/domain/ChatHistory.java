package com.core.market.chat.domain;

import com.core.market.user.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    private String message;

    private LocalDateTime createdAt;

    private ChatHistory(ChatRoom chatRoom, Member sender, String message, LocalDateTime createdAt) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static ChatHistory of(ChatRoom chatRoom, Member sender, String message, LocalDateTime createdAt) {
        return new ChatHistory(
                chatRoom,
                sender,
                message,
                createdAt
        );
    }
}
