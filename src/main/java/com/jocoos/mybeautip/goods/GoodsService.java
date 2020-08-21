package com.jocoos.mybeautip.goods;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
  private final TimeSaleService timeSaleService;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final StoreRepository storeRepository;
  private final DeliveryChargeOptionRepository deliveryChargeOptionRepository;
  private static final String BEST_CATEGORY = "001";

  @Value("${mybeautip.store.image-path.prefix}")
  private String storeImagePrefix;

  @Value("${mybeautip.store.image-path.refund-suffix}")
  private String storeImageRefundSuffix;

  @Value("${mybeautip.store.image-path.as-suffix}")
  private String storeImageAsSuffix;

    @Value("${mybeautip.goods.max-valid-state}")
  private Integer maxValidState;

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
                      TimeSaleService timeSaleService,
                      GoodsRepository goodsRepository,
                      VideoGoodsRepository videoGoodsRepository,
                      GoodsLikeRepository goodsLikeRepository,
                      StoreRepository storeRepository,
                      DeliveryChargeOptionRepository deliveryChargeOptionRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.timeSaleService = timeSaleService;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.storeRepository = storeRepository;
    this.deliveryChargeOptionRepository = deliveryChargeOptionRepository;
  }
  
  public Slice<Goods> getGoodsList(int count, Long cursor, String sort, String category) {
    FILTER filter  = getRequestFilter(category, sort);
    log.debug("GetGoodsList filter by: " + filter.toString());
  
    Date dateCursor = (cursor == null) ? new Date() : new Date(cursor);
  
    Slice<Goods> slice;
    switch (filter) {
      case CATEGORY:
        slice = goodsRepository.findAllByCategory(generateSearchableCategory(category), dateCursor, maxValidState, of(0, count));
        break;
        
      case SORT:
        slice = getGoodsBySort(sort, cursor, count);
        break;
        
      case CATEGORY_AND_SORT:
        slice = getGoodsBySortAndCategory(sort, category, cursor, count);
        break;
        
      case ALL:
      default:
        slice = goodsRepository.getGoodsList(dateCursor, maxValidState, of(0, count));
        break;
    }
    return slice;
  }
  
  private Slice<Goods> getGoodsBySort(String sort, Long cursor, int count) {
    int descCursor = (cursor == null) ? Integer.MAX_VALUE : cursor.intValue();
    int ascCursor = (cursor == null) ? Integer.MIN_VALUE : cursor.intValue();

    Slice<Goods> slice;
    switch (sort) {
      case "like":
        slice = goodsRepository.getGoodsListOrderByLikeCountDesc(descCursor, maxValidState, of(0, count));
        break;
      case "order":
        slice = goodsRepository.getGoodsListOrderByOrderCountDesc(descCursor, maxValidState, of(0, count));
        break;
      case "hit":
        slice = goodsRepository.getGoodsListOrderByHitCntDesc(descCursor, maxValidState, of(0, count));
        break;
      case "review":
        slice = goodsRepository.getGoodsListOrderByReviewCntDesc(descCursor, maxValidState, of(0, count));
        break;
      case "high-price":
        slice = goodsRepository.getGoodsListOrderByGoodsPriceDesc(descCursor, maxValidState, of(0, count));
        break;
      case "low-price":
        slice = goodsRepository.getGoodsListOrderByGoodsPriceAsc(ascCursor, maxValidState, of(0, count));
        break;
      case "latest":
      default:
        Date dateCursor = (cursor == null) ? new Date() : new Date(cursor);
        slice = goodsRepository.getGoodsListOrderByCreatedAtDesc(dateCursor, maxValidState, of(0, count));
        break;
    }
    return slice;
  }
  
  private Slice<Goods> getGoodsBySortAndCategory(String sort, String category, Long cursor, int count) {
    int descCursor = (cursor == null) ? Integer.MAX_VALUE : cursor.intValue();
    int ascCursor = (cursor == null) ? Integer.MIN_VALUE : cursor.intValue();

    Slice<Goods> slice;
    switch (sort) {
      case "like":
        slice = goodsRepository.getGoodsListCategoryAndOrderByLikeCountDesc(
            generateSearchableCategory(category), descCursor, maxValidState, of(0, count));
        break;
      case "order":
        slice = goodsRepository.getGoodsListCategoryAndOrderByOrderCountDesc(
            generateSearchableCategory(category), descCursor, maxValidState, of(0, count));
        break;
      case "hit":
        slice = goodsRepository.getGoodsListCategoryAndOrderByHitCntDesc(
            generateSearchableCategory(category), descCursor, maxValidState, of(0, count));
        break;
      case "review":
        slice = goodsRepository.getGoodsListCategoryAndOrderByReviewCntDesc(
            generateSearchableCategory(category), descCursor, maxValidState, of(0, count));
        break;
      case "high-price":
        slice = goodsRepository.getGoodsListCategoryAndOrderByGoodsPriceDesc(
            generateSearchableCategory(category), descCursor, maxValidState, of(0, count));
        break;
      case "low-price":
        slice = goodsRepository.getGoodsListCategoryAndOrderByGoodsPriceAsc(
            generateSearchableCategory(category), ascCursor, maxValidState, of(0, count));
        break;
      case "latest":
      default:
        Date dateCursor = (cursor == null) ? new Date() : new Date(cursor);
        slice = goodsRepository.getGoodsListCategoryAndOrderByCreatedAtDesc(
            generateSearchableCategory(category), dateCursor, maxValidState, of(0, count));
        break;
    }
    return slice;
  }

  public Slice<Goods>  findAllByKeyword(String keyword, Long cursor, int count) {
      return goodsRepository.findAllByKeyword(keyword, maxValidState, of(cursor.intValue(), count));
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
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public GoodsLike addLike(Goods goods) {
    goodsRepository.updateLikeCount(goods.getGoodsNo(), 1);
    goods.setLikeCount(goods.getLikeCount() + 1);
    return goodsLikeRepository.save(new GoodsLike(goods));
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void removeLike(GoodsLike liked) {
    goodsLikeRepository.delete(liked);
    if (liked.getGoods().getLikeCount() > 0) {
      goodsRepository.updateLikeCount(liked.getGoods().getGoodsNo(), -1);
    }
  }
  
  public Optional<GoodsInfo> generateGoodsInfo(String goodsNo) {
    return goodsRepository.findByGoodsNo(goodsNo).map(this::generateGoodsInfo);
  }

  public GoodsInfo generateGoodsInfo(Goods goods) {
    return generateGoodsInfo(goods, TimeSaleCondition.createGeneral());
  }

  public Optional<GoodsInfo> generateGoodsInfo(String goodsNo, TimeSaleCondition timeSaleCondition) {
    return goodsRepository.findByGoodsNo(goodsNo).map(g -> generateGoodsInfo(g, timeSaleCondition));
  }

  public GoodsInfo generateGoodsInfo(Goods goods, TimeSaleCondition timeSaleCondition) {
    timeSaleService.applyTimeSale(goods, timeSaleCondition);
    return generateGoodsInfo0(goods);
  }

  private GoodsInfo generateGoodsInfo0(Goods goods) {
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

    String extraInfo = null;
    Optional<DeliveryChargeOption> deliveryChargeOption = deliveryChargeOptionRepository.findByDeliveryChargeId(goods.getDeliverySno());
    if (deliveryChargeOption.isPresent()) {
      extraInfo = deliveryChargeOption.get().getExtraInfo();
    }

    return new GoodsInfo(goods, likeId, relatedVideoTotalCount, deliveryInfo, refundInfo, companyInfo, extraInfo);
  }

  private String generateSearchableCategory(String category) {
    return "|".concat(category).concat("|");
  }
}