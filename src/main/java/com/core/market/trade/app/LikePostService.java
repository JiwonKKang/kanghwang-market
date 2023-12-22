package com.core.market.trade.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.trade.api.response.TradePostDTO;
import com.core.market.trade.domain.LikePost;
import com.core.market.trade.domain.LikePostPK;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.repository.LikePostRepository;
import com.core.market.trade.domain.repository.TradePostRepository;
import com.core.market.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikePostService {

    private final LikePostRepository likePostRepository;
    private final TradePostRepository tradePostRepository;

    @Transactional
    public void likeTradePost(Long postId, Member member) {



        TradePost tradePost = tradePostRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));

        LikePostPK likePostPK = LikePostPK.of(member, tradePost); //postId와 userId로 복합키 생성

        /*
        같은 사람이 한번더 누를 경우 좋아요 취소로직
        * */
        likePostRepository.findById(likePostPK).ifPresentOrElse(
                        likePostRepository::delete,
                        () -> likePostRepository.save(LikePost.from(likePostPK)));
    }

    public int countLike(Long postId) {
        return likePostRepository.countLikeByPostId(postId);
    }

    public Page<TradePostDTO> getLikePostList(Member member, Pageable pageable) {

        return likePostRepository.getLikePostListByUserId(member.getId(), pageable);
    }
}
