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
        String mainImageUrl,
        int likeCount,
        int views,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static TradePostResponse from(TradePostDTO dto) {
        return new TradePostResponse(
                dto.postId(),
                dto.seller(),
                dto.title(),
                dto.content(),
                dto.price(),
                dto.tradeStatus(),
                dto.imageUrls().get(0),
                dto.likeCount(),
                dto.views(),
                dto.createdAt(),
                dto.modifiedAt()
        );
    }
}