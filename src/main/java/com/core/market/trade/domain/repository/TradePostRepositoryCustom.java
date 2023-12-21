package com.core.market.trade.domain.repository;

import com.core.market.trade.api.request.PostSearchCond;
import com.core.market.trade.api.response.TradePostDTO;
import com.core.market.user.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradePostRepositoryCustom {

    Page<TradePostDTO> search(PostSearchCond postSearchCond, Member member, Pageable pageable);

}
