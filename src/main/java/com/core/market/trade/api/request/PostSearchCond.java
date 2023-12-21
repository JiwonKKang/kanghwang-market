package com.core.market.trade.api.request;

public record PostSearchCond(
        String keyword,
        Boolean isTrading
) {
}
