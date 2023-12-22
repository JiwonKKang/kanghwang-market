package com.core.market.trade.api;

import com.core.market.common.Response;
import com.core.market.trade.api.request.PostSearchCond;
import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.api.request.TradePostEditRequest;
import com.core.market.trade.api.response.PostDetailResponse;
import com.core.market.trade.api.response.TradePostResponse;
import com.core.market.trade.app.TradePostService;
import com.core.market.trade.domain.TradePost;
import com.core.market.user.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trade-posts")
@Tag(name = "거래글 API", description = "중고 거래글 API입니다.")
public class TradePostController {

    private final TradePostService tradePostService;

    @GetMapping("/{postId}")
    @Operation(summary = "거래글 상세 조회")
    public Response<PostDetailResponse> getTradePost(@PathVariable Long postId) {
        return Response.success(PostDetailResponse.from(tradePostService.getTradePost(postId)));
    }

    @PostMapping
    @Operation(summary = "거래글 생성")
    public Response<Void> createTradePost(@RequestPart TradePostCreateRequest request,
                                          @RequestPart(required = false) List<MultipartFile> files,
                                          @AuthenticationPrincipal Member member) {
        tradePostService.createTradePost(request, files, member.getId());
        return Response.success();
    }


    @GetMapping
    @Operation(summary = "거래글 목록 검색")
    public Response<Page<TradePostResponse>> getTradePostPage(
            PostSearchCond searchCond,
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TradePostResponse> responses = tradePostService.getTradePostPage(searchCond, member.getId(), pageable)
                .map(TradePostResponse::from);
        return Response.success(responses);
    }

    @DeleteMapping("/{postId}")
    public Response<Void> deleteTradePost(@PathVariable Long postId,
                                          @AuthenticationPrincipal Member member) {
        tradePostService.deleteTradePost(postId, member);
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<Void> editTradePost(@PathVariable Long postId,
                                        @RequestPart TradePostEditRequest request,
                                        @RequestPart(required = false) List<MultipartFile> files,
                                        @AuthenticationPrincipal Member member) {

        tradePostService.editTradePost(postId, request, files, member);
        return Response.success();
    }
}
