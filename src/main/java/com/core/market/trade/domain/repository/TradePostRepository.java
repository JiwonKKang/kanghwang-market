package com.core.market.trade.domain.repository;

import com.core.market.trade.domain.TradePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradePostRepository extends JpaRepository<TradePost, Long>, TradePostRepositoryCustom {

}
