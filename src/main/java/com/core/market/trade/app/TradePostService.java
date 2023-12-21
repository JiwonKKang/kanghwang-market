package com.core.market.trade.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.trade.api.request.PostSearchCond;
import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.api.response.TradePostDTO;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import com.core.market.trade.domain.repository.TradePostRepository;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradePostService {

    private final TradePostRepository tradePostRepository;
    private final ImageUploadService imageUploadService;
    private final MemberService memberService;


    @Transactional
    public void createTradePost(TradePostCreateRequest request, List<MultipartFile> files, Long id) {

        Member member = memberService.findById(id);

        TradePost tradePost = TradePost.builder()
                .title(request.title())
                .user(member)
                .price(request.price())
                .content(request.content())
                .build();

        if (files != null) {
            List<String> imageUrlList = imageUploadService.uploadImagesInStorage(files);
            List<TradePostImage> postImages = imageUrlList.stream().map(TradePostImage::from).toList();
            tradePost.addAllImages(postImages);
        }

        tradePostRepository.save(tradePost);
    }


    @Transactional
    public TradePostDTO getTradePost(Long postId) {
        TradePost tradePost = tradePostRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));

        tradePost.viewCountUp(); /* JPA dirty checking 에 의하여 자동으로 update */

        Integer likeCount = tradePostRepository.countLikeByPostId(postId);

        return TradePostDTO.from(tradePost, likeCount);

    }

    public Page<TradePostDTO> getTradePostPage(PostSearchCond searchCond, Long memberId, Pageable pageable) {
        Member me = memberService.findById(memberId);
        return tradePostRepository.search(searchCond, me, pageable);
    }


}
