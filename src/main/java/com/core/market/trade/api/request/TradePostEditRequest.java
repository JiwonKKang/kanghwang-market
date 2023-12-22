package com.core.market.trade.api.request;

public record TradePostEditRequest(
        String title,
        String content,
        Integer price
) {
}
