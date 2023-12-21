package com.core.market.trade.api.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.core.market.trade.api.response.QTradePostDTO is a Querydsl Projection type for TradePostDTO
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QTradePostDTO extends ConstructorExpression<TradePostDTO> {

    private static final long serialVersionUID = -2040848006L;

    public QTradePostDTO(com.querydsl.core.types.Expression<? extends com.core.market.trade.domain.TradePost> post, com.querydsl.core.types.Expression<Long> likeCount) {
        super(TradePostDTO.class, new Class<?>[]{com.core.market.trade.domain.TradePost.class, long.class}, post, likeCount);
    }

}

