package com.core.market.chat.domain;

import com.core.market.common.BaseTimeEntity;
import com.core.market.trade.domain.TradePost;
import com.core.market.user.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TradePost post;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatUserStatus sellerStatus = ChatUserStatus.OUT;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatUserStatus buyerStatus = ChatUserStatus.OUT;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatHistory> chatHistories = new ArrayList<>();

    public void sellerIn() {
        this.sellerStatus = ChatUserStatus.IN;
    }

    public void buyerIn() {
        this.buyerStatus = ChatUserStatus.IN;
    }

    public void sellerOut() {
        this.sellerStatus = ChatUserStatus.OUT;
    }

    public void buyerOut() {
        this.buyerStatus = ChatUserStatus.OUT;
    }

    public boolean isAllUserIn() {
        return buyerStatus == ChatUserStatus.IN && sellerStatus == ChatUserStatus.IN;
    }

}
