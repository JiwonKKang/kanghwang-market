package com.core.market.trade.api;

import com.core.market.common.Response;
import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.api.response.PostDetailResponse;
import com.core.market.trade.api.response.TradePostResponse;
import com.core.market.trade.app.TradePostService;
import com.core.market.trade.domain.TradePost;
import com.core.market.user.domain.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trade-posts")
public class TradePostController {

    private final TradePostService tradePostService;

    @Tag(name = "TradePost API", description = "거래글 API입니다.")
    @PostMapping
    public Response<Void> createTradePost(@RequestPart TradePostCreateRequest request,
                                          @RequestPart(required = false) List<MultipartFile> files,
                                          @AuthenticationPrincipal Member member) {
        tradePostService.createTradePost(request, files, member.getEmail());
        return Response.success();
    }

    @GetMapping("/{postId}")
    public Response<PostDetailResponse> getTradePost(@PathVariable Long postId) {
        return Response.success(PostDetailResponse.from(tradePostService.getTradePost(postId)));
    }


    @GetMapping
    public Response<Page<TradePostResponse>> getTradePostPage(
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TradePostResponse> responses = tradePostService.getTradePostPage(pageable)
                .map(TradePostResponse::from);
        return Response.success(responses);
    }
}
