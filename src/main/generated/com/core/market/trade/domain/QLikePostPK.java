package com.core.market.trade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikePostPK is a Querydsl query type for LikePostPK
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QLikePostPK extends BeanPath<LikePostPK> {

    private static final long serialVersionUID = -2135348958L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikePostPK likePostPK = new QLikePostPK("likePostPK");

    public final QTradePost post;

    public final com.core.market.user.domain.QMember user;

    public QLikePostPK(String variable) {
        this(LikePostPK.class, forVariable(variable), INITS);
    }

    public QLikePostPK(Path<? extends LikePostPK> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikePostPK(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikePostPK(PathMetadata metadata, PathInits inits) {
        this(LikePostPK.class, metadata, inits);
    }

    public QLikePostPK(Class<? extends LikePostPK> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QTradePost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new com.core.market.user.domain.QMember(forProperty("user"), inits.get("user")) : null;
    }

}

