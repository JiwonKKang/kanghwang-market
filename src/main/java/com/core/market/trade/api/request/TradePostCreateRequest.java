package com.core.market.trade.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TradePostCreateRequest(

        @Schema(description = "거래글 제목", nullable = false, example = "옷 판매합니다.")
        String title,

        @Schema(description = "거래글 내용", nullable = false)
        String content,

        @Schema(description = "가격", nullable = false, example = "35000")
        Integer price
) {
}
