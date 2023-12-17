package com.core.market.trade.domain;

import com.core.market.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikePost extends BaseTimeEntity {

    @EmbeddedId
    private LikePostPK likePostPK;

}
