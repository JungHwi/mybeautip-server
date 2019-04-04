package com.jocoos.mybeautip.goods;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import static org.springframework.data.domain.PageRequest.of;

import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
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
    Optional<Goods> optional = goodsRepository.findByGoodsNo(goodsNo);
    String category = BEST_CATEGORY;
    if (optional.isPresent()) {
      category = optional.get().getCateCd();
    }
    PageRequest pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.DESC, "id"));
    return goodsRepository.findRelatedGoods(category, goodsNo, pageable);
  }

  private enum FILTER {ALL, CATEGORY, SORT, CATEGORY_AND_SORT}
  
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
  
  public Slice<Goods> getGoodsList(int count, String cursor, String sort, String category) {
    FILTER filter  = getRequestFilter(category, sort);
    log.debug("GetGoodsList filter by: " + filter.toString());
  
    Date dateCursor = (Strings.isBlank(cursor)) ? new Date() : new Date(Long.parseLong(cursor));
  
    Slice<Goods> slice;
    switch (filter) {
      case CATEGORY:
        slice = goodsRepository.findAllByCategory(generateSearchableCategory(category), dateCursor, of(0, count));
        break;
        
      case SORT:
        slice = getGoodsBySort(sort, cursor, count);
        break;
        
      case CATEGORY_AND_SORT:
        slice = getGoodsBySortAndCategory(sort, category, cursor, count);
        break;
        
      case ALL:
      default:
        slice = goodsRepository.getGoodsList(dateCursor, of(0, count));
        break;
    }
    return slice;
  }
  
  private Slice<Goods> getGoodsBySort(String sort, String cursor, int count) {
    int descCursor = (Strings.isBlank(cursor)) ? Integer.MAX_VALUE : Integer.parseInt(cursor);
    int ascCursor = (Strings.isBlank(cursor)) ? Integer.MIN_VALUE : Integer.parseInt(cursor);
    
    Slice<Goods> slice;
    switch (sort) {
      case "like":
        slice = goodsRepository.getGoodsListOrderByLikeCountDesc(descCursor, of(0, count));
        break;
      case "order":
        slice = goodsRepository.getGoodsListOrderByOrderCountDesc(descCursor, of(0, count));
        break;
      case "hit":
        slice = goodsRepository.getGoodsListOrderByHitCntDesc(descCursor, of(0, count));
        break;
      case "review":
        slice = goodsRepository.getGoodsListOrderByReviewCntDesc(descCursor, of(0, count));
        break;
      case "high-price":
        slice = goodsRepository.getGoodsListOrderByGoodsPriceDesc(descCursor, of(0, count));
        break;
      case "low-price":
        slice = goodsRepository.getGoodsListOrderByGoodsPriceAsc(ascCursor, of(0, count));
        break;
      case "latest":
      default:
        Date dateCursor = (Strings.isBlank(cursor)) ? new Date() : new Date(Long.parseLong(cursor));
        slice = goodsRepository.getGoodsListOrderByCreatedAtDesc(dateCursor, of(0, count));
        break;
    }
    return slice;
  }
  
  private Slice<Goods> getGoodsBySortAndCategory(String sort, String category, String cursor, int count) {
    int descCursor = (Strings.isBlank(cursor)) ? Integer.MAX_VALUE : Integer.parseInt(cursor);
    int ascCursor = (Strings.isBlank(cursor)) ? Integer.MIN_VALUE : Integer.parseInt(cursor);
    
    Slice<Goods> slice;
    switch (sort) {
      case "like":
        slice = goodsRepository.getGoodsListCategoryAndOrderByLikeCountDesc(
            generateSearchableCategory(category), descCursor, of(0, count));
        break;
      case "order":
        slice = goodsRepository.getGoodsListCategoryAndOrderByOrderCountDesc(
            generateSearchableCategory(category), descCursor, of(0, count));
        break;
      case "hit":
        slice = goodsRepository.getGoodsListCategoryAndOrderByHitCntDesc(
            generateSearchableCategory(category), descCursor, of(0, count));
        break;
      case "review":
        slice = goodsRepository.getGoodsListCategoryAndOrderByReviewCntDesc(
            generateSearchableCategory(category), descCursor, of(0, count));
        break;
      case "high-price":
        slice = goodsRepository.getGoodsListCategoryAndOrderByGoodsPriceDesc(
            generateSearchableCategory(category), descCursor, of(0, count));
        break;
      case "low-price":
        slice = goodsRepository.getGoodsListCategoryAndOrderByGoodsPriceAsc(
            generateSearchableCategory(category), ascCursor, of(0, count));
        break;
      case "latest":
      default:
        Date dateCursor = (Strings.isBlank(cursor)) ? new Date() : new Date(Long.parseLong(cursor));
        slice = goodsRepository.getGoodsListCategoryAndOrderByCreatedAtDesc(
            generateSearchableCategory(category), dateCursor, of(0, count));
        break;
    }
    return slice;
  }

  private FILTER getRequestFilter(String category, String sort) {
    if (Strings.isEmpty(category) && Strings.isEmpty(sort)) {
      return FILTER.ALL;
    }
    if (Strings.isNotEmpty(category) && Strings.isEmpty(sort)) {
      return FILTER.CATEGORY;
    }
    if (Strings.isNotEmpty(sort) && Strings.isEmpty(category)) {
      return FILTER.SORT;
    }
    if (Strings.isNotEmpty(sort) && Strings.isNotEmpty(category)) {
      return FILTER.CATEGORY_AND_SORT;
    }

    return FILTER.ALL;
  }
  
  @Transactional
  public GoodsLike addLike(Goods goods) {
    goodsRepository.updateLikeCount(goods.getGoodsNo(), 1);
    goods.setLikeCount(goods.getLikeCount() + 1);
    return goodsLikeRepository.save(new GoodsLike(goods));
  }
  
  @Transactional
  public void removeLike(GoodsLike liked) {
    goodsLikeRepository.delete(liked);
    goodsRepository.updateLikeCount(liked.getGoods().getGoodsNo(), -1);
  }
  
  public Optional<GoodsInfo> generateGoodsInfo(String goodsNo) {
    return goodsRepository.findByGoodsNo(goodsNo).map(this::generateGoodsInfo);
  }

  public GoodsInfo generateGoodsInfo(Goods goods) {
    // Set like ID if exist
    Long likeId = null;
    Long me = memberService.currentMemberId();
    if (me != null) {
      Optional<GoodsLike> optional = goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goods.getGoodsNo(), me);
      likeId = optional.map(GoodsLike::getId).orElse(null);
    }
    // Set total count of related videos
    int relatedVideoTotalCount = videoGoodsRepository
        .countByGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(goods.getGoodsNo(), "PUBLIC", "CREATED");
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