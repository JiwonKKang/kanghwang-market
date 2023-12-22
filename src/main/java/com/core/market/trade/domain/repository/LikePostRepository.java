package com.core.market.trade.domain.repository;

import com.core.market.trade.domain.LikePost;
import com.core.market.trade.domain.LikePostPK;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, LikePostPK>, LikePostRepositoryCustom  {

    @Query(value = "select COUNT(*) from LikePost lp where lp.likePostPK.post.id = :postId")
    Integer countLikeByPostId(@Param("postId") Long postId);


}
