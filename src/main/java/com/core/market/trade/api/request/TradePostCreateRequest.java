package com.core.market.trade.api.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record TradePostCreateRequest(
        String title,
        String content,
        List<MultipartFile> files
) {
}
