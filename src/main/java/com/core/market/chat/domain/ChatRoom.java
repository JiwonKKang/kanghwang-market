package com.core.market.chat.domain;

import com.core.market.common.BaseTimeEntity;
import com.core.market.trade.domain.TradePost;
import com.core.market.user.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TradePost post;

    @ManyToOne
    private Member seller;

    @ManyToOne
    private Member buyer;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatHistory> chatHistories = new ArrayList<>();


}
