package com.core.market.trade.api.response;

import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import com.core.market.trade.domain.TradeStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.List;

public record TradePostDTO(
        Long postId,
        UserResponse seller,
        String title,
        String content,
        Integer price,
        TradeStatus tradeStatus,
        List<String> imageUrls,
        int likeCount,
        int views,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static TradePostDTO from(TradePost post, Integer likeCount) {
        return new TradePostDTO(
                post.getId(),
                UserResponse.from(post.getUser()),
                post.getTitle(),
                post.getContent(),
                post.getPrice(),
                post.getTradeStatus(),
                post.getTradePostImages().stream()
                        .map(TradePostImage::getImageUrl)
                        .toList(),
                likeCount,
                post.getViews(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

    @QueryProjection
    public TradePostDTO(TradePost post, Long likeCount) {
        this(
                post.getId(),
                UserResponse.from(post.getUser()),
                post.getTitle(),
                post.getContent(),
                post.getPrice(),
                post.getTradeStatus(),
                post.getTradePostImages()
                        .stream().map(TradePostImage::getImageUrl)
                        .toList(),
                likeCount.intValue(),
                post.getViews(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
