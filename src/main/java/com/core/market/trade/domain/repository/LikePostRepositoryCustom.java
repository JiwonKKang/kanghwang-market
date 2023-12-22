package com.core.market.trade.domain.repository;

import com.core.market.trade.api.response.TradePostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikePostRepositoryCustom {

    Page<TradePostDTO> getLikePostListByUserId(Long userId, Pageable pageable);

}
