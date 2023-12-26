package com.core.market.chat.domain;

import com.core.market.user.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    private String message;

    private int unreadCount;

    private LocalDateTime createdAt;

    private ChatHistory(ChatRoom chatRoom, Member sender, String message, int unreadCount, LocalDateTime createdAt) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.unreadCount = unreadCount;
        this.createdAt = createdAt;
    }

    public static ChatHistory of(ChatRoom chatRoom, Member sender, String message, int unreadCount, LocalDateTime createdAt) {
        return new ChatHistory(
                chatRoom,
                sender,
                message,
                unreadCount,
                createdAt
        );
    }
}
