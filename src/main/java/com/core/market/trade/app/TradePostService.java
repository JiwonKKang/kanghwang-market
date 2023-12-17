package com.core.market.trade.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.api.response.TradePostResponse;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import com.core.market.trade.domain.TradePostRepository;
import com.core.market.user.domain.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradePostService {

    private final TradePostRepository tradePostRepository;
    private final ImageUploadService imageUploadService;


    @Transactional
    public void createTradePost(TradePostCreateRequest request) {

        List<String> imageUrlList = imageUploadService.uploadImagesInStorage(request.files());

        TradePost tradePost = TradePost.builder()
                .title(request.title())
                .user(Users.of("jiwon", "청주시 흥덕구 봉명동", coordinateToPoint(1.0, 1.0) )) //TODO : Antentication으로 유저 추가
                .price(request.price())
                .content(request.content())
                .build();

        List<TradePostImage> postImages = imageUrlList.stream().map(TradePostImage::from).toList();
        tradePost.addAllImages(postImages);
        System.out.println(org.hibernate.Version.getVersionString());
        tradePostRepository.save(tradePost);
    }


    @Transactional
    public TradePostResponse getTradePost(Long postId) {
        TradePost tradePost = tradePostRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));

        tradePost.viewCountUp(); /* JPA dirty checking 에 의하여 자동으로 update */

        Integer likeCount = tradePostRepository.countLikeByPostId(postId);

        return TradePostResponse.from(tradePost, likeCount);

    }

    private Point coordinateToPoint(Double x, Double y) { //TODO: 나중에 UserService로 이동해서 유저 저장할떄 사용
        String pointWKT = String.format("POINT(%f %f)", x, y);
        try {
            return (Point) new WKTReader().read(pointWKT);
        } catch (ParseException e) {
            log.warn("좌표변환중 오류 발생");
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
    }
}
