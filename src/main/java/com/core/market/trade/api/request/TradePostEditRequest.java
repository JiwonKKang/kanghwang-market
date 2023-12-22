package com.core.market.trade.api.request;

import java.util.List;

public record TradePostEditRequest(
        String title,
        String content,
        Integer price,
        List<String> removeImageUrlList
) {
}
