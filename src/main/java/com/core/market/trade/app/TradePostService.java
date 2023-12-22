package com.core.market.trade.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.trade.api.request.PostSearchCond;
import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.api.request.TradePostEditRequest;
import com.core.market.trade.api.response.TradePostDTO;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import com.core.market.trade.domain.repository.TradePostRepository;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import com.nimbusds.jose.util.Pair;
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
    private final LikePostService likePostService;
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

        Integer likeCount = likePostService.countLike(postId);

        return TradePostDTO.from(tradePost, likeCount);

    }

    public Page<TradePostDTO> getTradePostPage(PostSearchCond searchCond, Long memberId, Pageable pageable) {
        Member me = memberService.findById(memberId);
        return tradePostRepository.search(searchCond, me, pageable);
    }


    public void deleteTradePost(Long postId, Member member) {

        Pair<TradePost, Boolean> postAndIsOwner = isOwner(postId, member.getId());
        if (!postAndIsOwner.getRight()) {
            throw new CustomException(ErrorCode.NO_PERMISSION_ERROR);
        }
        tradePostRepository.delete(postAndIsOwner.getLeft());
    }

    @Transactional
    public void editTradePost(Long postId,
                              TradePostEditRequest request,
                              List<MultipartFile> files,
                              Member member) {
        Pair<TradePost, Boolean> postAndIsOwner = isOwner(postId, member.getId());

        if (!postAndIsOwner.getRight()) {
            throw new CustomException(ErrorCode.NO_PERMISSION_ERROR);
        }

        tradePostRepository.deleteAllByImageUrl(request.removeImageUrlList());
        log.info("사용자가 삭제한 이미지 파일 - {}", request.removeImageUrlList());

        if (files != null) {
            List<String> imageUrlList = imageUploadService.uploadImagesInStorage(files);
            List<TradePostImage> postImages = imageUrlList.stream().map(TradePostImage::from).toList();
            postAndIsOwner.getLeft().addAllImages(postImages);
            log.info("업로드 파일 확인 : 이미지 저장 완료 - {}", imageUrlList);
        }

        postAndIsOwner.getLeft().editTradePost(request);
    }

    private Pair<TradePost, Boolean> isOwner(Long postId, Long memberId) { //삭제 및 수정 권한이있는지 확인, 즉 주인인지 확인
        TradePost tradePost = tradePostRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));

        return Pair.of(tradePost, tradePost.getUser().getId().equals(memberId));
    }
}
