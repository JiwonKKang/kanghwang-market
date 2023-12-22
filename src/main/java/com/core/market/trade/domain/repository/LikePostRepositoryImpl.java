package com.core.market.trade.domain.repository;

import com.core.market.trade.api.response.QTradePostDTO;
import com.core.market.trade.api.response.TradePostDTO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.core.market.trade.domain.QLikePost.likePost;
import static com.core.market.trade.domain.QTradePost.tradePost;

@RequiredArgsConstructor
@Repository
public class LikePostRepositoryImpl implements LikePostRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<TradePostDTO> getLikePostListByUserId(Long userId, Pageable pageable) {

        List<TradePostDTO> contents = queryFactory.select(new QTradePostDTO(
                        tradePost,
                        ExpressionUtils.as(JPAExpressions.select(likePost.count())
                                .from(likePost)
                                .where(likePost.likePostPK.post.eq(tradePost)), "likeCount")))
                .from(likePost)
                .join(likePost.likePostPK.post, tradePost)
                .where(likePost.likePostPK.user.id.eq(userId))
                .orderBy(likePost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory.select(tradePost.count())
                .from(likePost)
                .join(likePost.likePostPK.post, tradePost)
                .where(likePost.likePostPK.user.id.eq(userId));

        return PageableExecutionUtils.getPage(contents, pageable, count::fetchOne);
    }
}
