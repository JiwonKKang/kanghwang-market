package com.core.market.trade.domain;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {

    @Query(value = "select COUNT(*) from LikePost lp where lp.likePostPK.post.id = :postId")
    Integer countLikeByPostId(@Param("postId") Long postId);


}
