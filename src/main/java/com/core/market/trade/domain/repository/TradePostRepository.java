package com.core.market.trade.domain.repository;

import com.core.market.trade.domain.TradePost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TradePostRepository extends JpaRepository<TradePost, Long>, TradePostRepositoryCustom {

    @Query(value = "select COUNT(*) from LikePost lp where lp.likePostPK.post.id = :postId")
    Integer countLikeByPostId(@Param("postId") Long postId);


}
