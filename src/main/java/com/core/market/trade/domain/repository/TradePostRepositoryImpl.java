package com.core.market.trade.domain.repository;

import com.core.market.trade.api.request.PostSearchCond;
import com.core.market.trade.api.response.QTradePostDTO;
import com.core.market.trade.api.response.TradePostDTO;
import com.core.market.trade.domain.TradeStatus;
import com.core.market.user.domain.Member;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
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
import static com.core.market.user.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class TradePostRepositoryImpl implements TradePostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TradePostDTO> search(PostSearchCond postSearchCond, Member me, Pageable pageable) { //회원이 설정한 범위안에있는 중고 거래글 검색

        NumberTemplate<Double> calculate_distance = Expressions.numberTemplate(Double.class, "ST_Distance_Sphere({0}, {1})", member.point, me.getPoint());
        //거리 계산 MySQL 내장함수

        List<TradePostDTO> contents =
                queryFactory.select(new QTradePostDTO(
                                tradePost,
                                ExpressionUtils.as(JPAExpressions.select(likePost.count())
                                        .from(likePost)
                                        .where(likePost.likePostPK.post.eq(tradePost)), "likeCount"))
                                )
                        .from(tradePost)
                        .leftJoin(tradePost.user, member)
                        .where(
                                containKeyword(postSearchCond.keyword()),
                                calculate_distance.loe(me.getSearchScope().getDistance()),
                                isTrading(postSearchCond.isTrading())
                        )
                        .orderBy(tradePost.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        JPAQuery<Long> count = queryFactory.select(tradePost.count())
                .from(tradePost);

        return PageableExecutionUtils.getPage(contents, pageable, count::fetchOne);

    }

    private BooleanExpression containKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        return tradePost.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression isTrading(Boolean isTrading) {
        if (isTrading == null || isTrading.describeConstable().isEmpty() || isTrading) {
            return tradePost.tradeStatus.eq(TradeStatus.TRADING).and(tradePost.removedAt.isNull());
        }
        return tradePost.removedAt.isNull(); // isNull -> 삭제되지않은것 즉, 삭제된것은 보여주지않는다
    }

}
