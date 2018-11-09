package com.jocoos.mybeautip.goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import static org.springframework.data.domain.PageRequest.of;

import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreRepository;
import com.jocoos.mybeautip.video.VideoGoodsRepository;

@Service
@Slf4j
public class GoodsService {
  private final MemberService memberService;
  private final MessageService messageService;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final StoreRepository storeRepository;
  private static final String BEST_CATEGORY = "001";

  @Value("${mybeautip.store.image-path.prefix}")
  private String storeImagePrefix;

  @Value("${mybeautip.store.image-path.refund-suffix}")
  private String storeImageRefundSuffix;

  @Value("${mybeautip.store.image-path.as-suffix}")
  private String storeImageAsSuffix;


  public List<Goods> getRelatedGoods(String goodsNo) {
    Optional<Goods> optional = goodsRepository.findByGoodsNoAndGoodsDisplayFlAndDeletedAtIsNull(goodsNo, "y");
    String category = BEST_CATEGORY;
    if (optional.isPresent()) {
      category = optional.get().getCateCd();
    }
    PageRequest pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.DESC, "id"));
    return goodsRepository.findRelatedGoods(category, goodsNo, pageable);
  }

  private enum FILTER {ALL, CATEGORY, KEYWORD, CATEGORY_AND_KEYWORD}
  
  public GoodsService(MemberService memberService,
                      MessageService messageService,
                      GoodsRepository goodsRepository,
                      VideoGoodsRepository videoGoodsRepository,
                      GoodsLikeRepository goodsLikeRepository,
                      StoreRepository storeRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.storeRepository = storeRepository;
  }
  
  public CursorResponse getGoodsList(GoodsListRequest request) {
    Date startCursor = (Strings.isBlank(request.getCursor())) ?
        new Date() : new Date(Long.parseLong(request.getCursor()));
    
    List<GoodsInfo> result = new ArrayList<>();
    Slice<Goods> slice = null;
    
    FILTER filter  = getRequestFilter(request);
    log.debug("GetGoodsList filter by: " + filter.toString());
    switch (filter) {
      case ALL:
        slice = goodsRepository.getGoodsList(startCursor,
            of(0, request.getCount()));
        break;
      case CATEGORY:
        slice = goodsRepository.findAllByCategory(
          generateSearchableCategory(request.getCategory()),
          startCursor, of(0, request.getCount()));
        break;
        
      case KEYWORD:
        slice = goodsRepository.findAllByKeyword(request.getKeyword(), startCursor,
            of(0, request.getCount()));
        break;
        
      case CATEGORY_AND_KEYWORD:
        slice = goodsRepository.findAllByCategoryAndKeyword(
          generateSearchableCategory(request.getCategory()),
          request.getKeyword(), startCursor, of(0, request.getCount()));
        break;
      
      default:
        break;
    }
  
    if (slice != null && slice.hasContent()) {
      for (Goods goods : slice.getContent()) {
        result.add(generateGoodsInfo(goods));
      }
    }

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/goods", result)
      .withCount(request.getCount())
      .withCursor(nextCursor)
      .withCategory(request.getCategory())
      .withKeyword(request.getKeyword()).toBuild();
  }

  private FILTER getRequestFilter(GoodsListRequest request) {
    if (Strings.isEmpty(request.getCategory()) && Strings.isEmpty(request.getKeyword())) {
      return FILTER.ALL;
    }
    if (Strings.isNotEmpty(request.getCategory()) && Strings.isEmpty(request.getKeyword())) {
      return FILTER.CATEGORY;
    }
    if (Strings.isNotEmpty(request.getKeyword()) && Strings.isEmpty(request.getCategory())) {
      return FILTER.KEYWORD;
    }
    if (Strings.isNotEmpty(request.getKeyword()) && Strings.isNotEmpty(request.getCategory())) {
      return FILTER.CATEGORY_AND_KEYWORD;
    }

    return FILTER.ALL;
  }

  public GoodsInfo generateGoodsInfo(Goods goods) {
    // Set like ID if exist
    Long likeId = null;
    Long me = memberService.currentMemberId();
    if (me != null) {
      Optional<GoodsLike> optional = goodsLikeRepository.findByGoodsGoodsNoAndCreatedByIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(goods.getGoodsNo(), me, "y");
      likeId = optional.map(GoodsLike::getId).orElse(null);
    }
    // Set total count of related videos
    int relatedVideoTotalCount = videoGoodsRepository
        .countByGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(goods.getGoodsNo(), "PUBLIC", "y");
    String deliveryInfo = "";
    String refundInfo = "";
    String asInfo = "";
    String companyInfo = messageService.getGoodsCompanyMessage();

    Optional<Store> optional = storeRepository.findById(goods.getScmNo());
    if (optional.isPresent()) {
      deliveryInfo = optional.get().getDeliveryInfo();
      refundInfo = optional.get().getCancelInfo();
    }

    return new GoodsInfo(goods, likeId, relatedVideoTotalCount, deliveryInfo, refundInfo, companyInfo);
  }

  private String generateSearchableCategory(String category) {
    return "|".concat(category).concat("|");
  }
}