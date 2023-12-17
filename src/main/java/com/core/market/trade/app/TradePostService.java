package com.core.market.trade.app;

import com.core.market.trade.api.request.TradePostCreateRequest;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.TradePostImage;
import com.core.market.trade.domain.TradePostRepository;
import com.core.market.user.domain.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradePostService {

    private final TradePostRepository tradePostRepository;
    private final ImageUploadService imageUploadService;


    public void createTradePost(TradePostCreateRequest request) {

        List<String> imageUrlList = imageUploadService.uploadImagesInStorage(request.files());

        List<TradePostImage> postImages = imageUrlList.stream().map(TradePostImage::from).toList();

        TradePost tradePost = TradePost.builder()
                .tradePostImages(postImages)
                .title(request.title())
                .user(Users.of("jiwon", "청주시 흥덕구 봉명동", 36.5))
                .content(request.content())
                .build();

        tradePostRepository.save(tradePost);
    }


}
