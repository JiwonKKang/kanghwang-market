package com.core.market.trade.api.response;

import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import com.core.market.trade.domain.TradeStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TradePostResponse(
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
    public static TradePostResponse from(TradePost post, Integer likeCount) {
        return new TradePostResponse(
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


}
