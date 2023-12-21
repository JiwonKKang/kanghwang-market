package com.core.market.trade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikePost is a Querydsl query type for LikePost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikePost extends EntityPathBase<LikePost> {

    private static final long serialVersionUID = 1794424039L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikePost likePost = new QLikePost("likePost");

    public final com.core.market.common.QBaseTimeEntity _super = new com.core.market.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QLikePostPK likePostPK;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> removedAt = _super.removedAt;

    public QLikePost(String variable) {
        this(LikePost.class, forVariable(variable), INITS);
    }

    public QLikePost(Path<? extends LikePost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikePost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikePost(PathMetadata metadata, PathInits inits) {
        this(LikePost.class, metadata, inits);
    }

    public QLikePost(Class<? extends LikePost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.likePostPK = inits.isInitialized("likePostPK") ? new QLikePostPK(forProperty("likePostPK"), inits.get("likePostPK")) : null;
    }

}

