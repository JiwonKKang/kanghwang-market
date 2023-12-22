package com.core.market.trade.domain;

import com.core.market.common.BaseTimeEntity;
import com.core.market.trade.api.request.TradePostEditRequest;
import com.core.market.user.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE trade_post SET removed_at = NOW() WHERE id = ?")
public class TradePost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Builder.Default
    private Integer price = 0;

    @Builder.Default
    private Integer views = 0;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private TradeStatus tradeStatus = TradeStatus.TRADING;

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<TradePostImage> tradePostImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Member user;

    public void addAllImages(List<TradePostImage> images) {
        for (TradePostImage image : images) {
            image.setPost(this);
        }

        tradePostImages.addAll(images);
    }

    public void editTradePost(TradePostEditRequest request) {
        this.title = request.title();
        this.content = request.content();
        this.price = request.price();
    }

    public void viewCountUp() {
        views++;
    }
}
