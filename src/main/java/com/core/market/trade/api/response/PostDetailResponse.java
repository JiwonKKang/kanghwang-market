package com.core.market.trade.api.response;

import com.core.market.trade.domain.TradeStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
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
    public static PostDetailResponse from(TradePostDTO dto) {
        return new PostDetailResponse(
                dto.postId(),
                dto.seller(),
                dto.title(),
                dto.content(),
                dto.price(),
                dto.tradeStatus(),
                dto.imageUrls(),
                dto.likeCount(),
                dto.views(),
                dto.createdAt(),
                dto.modifiedAt()
        );
    }
}
