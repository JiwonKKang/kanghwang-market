package com.core.market.trade.api;

import com.core.market.common.Response;
import com.core.market.trade.api.response.TradePostResponse;
import com.core.market.trade.app.LikePostService;
import com.core.market.user.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like-posts")
public class LikePostController {

    private final LikePostService likePostService;

    @PostMapping("/{postId}")
    @Operation(summary = "관심 거래글 생성(좋아요)")
    public Response<Integer> likeTradePost(@PathVariable Long postId, @AuthenticationPrincipal Member member) {

        likePostService.likeTradePost(postId, member);
        return Response.success(likePostService.countLike(postId));
    }

    @GetMapping
    @Operation(summary = "관심 거래글 목록 가져오기")
    public Response<Page<TradePostResponse>> likeTradePostList(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TradePostResponse> res = likePostService.getLikePostList(member, pageable)
                .map(TradePostResponse::from);

        return Response.success(res);
    }
}
