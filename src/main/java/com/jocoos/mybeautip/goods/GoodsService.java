package com.jocoos.mybeautip.goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import static org.springframework.data.domain.PageRequest.of;

import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;

@Service
@Slf4j
public class GoodsService {
  private final MemberService memberService;
  private final GoodsRepository goodsRepository;
  private final GoodsOptionRepository goodsOptionRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private static final String BEST_CATEGORY = "001";

  public List<Goods> getRelatedGoods(String goodsNo) {
    Optional<Goods> optional = goodsRepository.findByGoodsNo(goodsNo);
    String category = BEST_CATEGORY;
    if (optional.isPresent()) {
      category = optional.get().getCateCd();
    }
    PageRequest pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.DESC, "id"));
    return goodsRepository.findRelatedGoods(category, goodsNo, pageable);
  }

  public List<GoodsOption> getAllGoodsOptions(Integer goodsNo) {
    return goodsOptionRepository.findByGoodsNo(goodsNo);
  }

  private enum FILTER {ALL, CATEGORY, KEYWORD, CATEGORY_AND_KEYWORD}
  
  public GoodsService(MemberService memberService,
                      GoodsRepository goodsRepository,
                      GoodsOptionRepository goodsOptionRepository,
                      GoodsLikeRepository goodsLikeRepository) {
    this.memberService = memberService;
    this.goodsRepository = goodsRepository;
    this.goodsOptionRepository = goodsOptionRepository;
    this.goodsLikeRepository = goodsLikeRepository;
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

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getModifiedAt().getTime());
      return new CursorResponse.Builder<>("/api/1/goods", result)
        .withCount(request.getCount())
        .withCursor(nextCursor)
        .withCategory(request.getCategory())
        .withKeyword(request.getKeyword()).toBuild();
    } else {
      return new CursorResponse.Builder<>("/api/1/goods", result).toBuild();
    }
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
      Optional<GoodsLike> optional = goodsLikeRepository.findByGoodsGoodsNoAndCreatedBy(goods.getGoodsNo(), me);
      likeId = optional.map(GoodsLike::getId).orElse(null);
    }
    return new GoodsInfo(goods, likeId);
  }

  private String generateSearchableCategory(String category) {
    return "|".concat(category).concat("|");
  }
}