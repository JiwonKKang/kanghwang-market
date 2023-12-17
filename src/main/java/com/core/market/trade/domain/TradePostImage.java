package com.core.market.trade.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TradePostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_post_id")
    private TradePost post;

    private TradePostImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static TradePostImage from(String imageUrl) {
        return new TradePostImage(
                imageUrl
        );
    }
}
