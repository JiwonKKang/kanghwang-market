package com.core.market.trade.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
