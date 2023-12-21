package com.core.market.trade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTradePost is a Querydsl query type for TradePost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradePost extends EntityPathBase<TradePost> {

    private static final long serialVersionUID = 722962964L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTradePost tradePost = new QTradePost("tradePost");

    public final com.core.market.common.QBaseTimeEntity _super = new com.core.market.common.QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> removedAt = _super.removedAt;

    public final StringPath title = createString("title");

    public final ListPath<TradePostImage, QTradePostImage> tradePostImages = this.<TradePostImage, QTradePostImage>createList("tradePostImages", TradePostImage.class, QTradePostImage.class, PathInits.DIRECT2);

    public final EnumPath<TradeStatus> tradeStatus = createEnum("tradeStatus", TradeStatus.class);

    public final com.core.market.user.domain.QMember user;

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QTradePost(String variable) {
        this(TradePost.class, forVariable(variable), INITS);
    }

    public QTradePost(Path<? extends TradePost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTradePost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTradePost(PathMetadata metadata, PathInits inits) {
        this(TradePost.class, metadata, inits);
    }

    public QTradePost(Class<? extends TradePost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.core.market.user.domain.QMember(forProperty("user"), inits.get("user")) : null;
    }

}

