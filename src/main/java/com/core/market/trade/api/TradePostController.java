package com.core.market.trade.api;

import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.app.TradePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trade-posts")
public class TradePostController {

    private final TradePostService tradePostService;

    @PostMapping
    public String createTradePost(TradePostCreateRequest request) {
        tradePostService.createTradePost(request);
        return "success";
    }

}
