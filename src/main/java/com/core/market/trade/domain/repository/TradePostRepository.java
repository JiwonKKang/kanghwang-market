package com.core.market.trade.domain.repository;

import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TradePostRepository extends JpaRepository<TradePost, Long>, TradePostRepositoryCustom {


    @Modifying
    @Transactional
    @Query(value = "delete from TradePostImage ti where ti.imageUrl in :imageUrls")
    void deleteAllByImageUrl(List<String> imageUrls);
}
