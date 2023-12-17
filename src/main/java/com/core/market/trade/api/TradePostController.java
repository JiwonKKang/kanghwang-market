package com.core.market.trade.api;

import com.core.market.common.Response;
import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.api.response.TradePostResponse;
import com.core.market.trade.app.TradePostService;
import com.core.market.trade.domain.TradePost;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trade-posts")
public class TradePostController {

    private final TradePostService tradePostService;

    @PostMapping
    public Response<Void> createTradePost(TradePostCreateRequest request) {
        tradePostService.createTradePost(request);
        return Response.success();
    }

    @GetMapping("/{postId}")
    public Response<TradePostResponse> getTradePost(@PathVariable Long postId) {
        return Response.success(tradePostService.getTradePost(postId));
    }

}
