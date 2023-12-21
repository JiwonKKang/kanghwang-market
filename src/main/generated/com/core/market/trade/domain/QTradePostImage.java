package com.core.market.trade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTradePostImage is a Querydsl query type for TradePostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradePostImage extends EntityPathBase<TradePostImage> {

    private static final long serialVersionUID = -832087129L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTradePostImage tradePostImage = new QTradePostImage("tradePostImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final QTradePost post;

    public QTradePostImage(String variable) {
        this(TradePostImage.class, forVariable(variable), INITS);
    }

    public QTradePostImage(Path<? extends TradePostImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTradePostImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTradePostImage(PathMetadata metadata, PathInits inits) {
        this(TradePostImage.class, metadata, inits);
    }

    public QTradePostImage(Class<? extends TradePostImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QTradePost(forProperty("post"), inits.get("post")) : null;
    }

}

