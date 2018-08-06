package com.jocoos.mybeautip.goods;

import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
public class GoodsService {
  private final MemberService memberService;
  private final GoodsRepository goodsRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  
  private enum FILTER {ALL, CATEGORY, KEYWORD, CATEGORY_AND_KEYWORD};
  
  public GoodsService(MemberService memberService,
                      GoodsRepository goodsRepository,
                      GoodsLikeRepository goodsLikeRepository) {
    this.memberService = memberService;
    this.goodsRepository = goodsRepository;
    this.goodsLikeRepository = goodsLikeRepository;
  }
  
  public Response getGoodsList(GoodsListRequest request) {
    Date startCursor = (Strings.isBlank(request.getCursor())) ?
        new Date() : new Date(Long.parseLong(request.getCursor()));
    
    List<Goods> list = new ArrayList<>();
    Slice<Goods> slice = null;
    
    FILTER filter  = getRequestFilter(request);
    log.debug("GetGoodsList filter by: " + filter.toString());
    switch (filter) {
      case ALL:
        slice = goodsRepository.getGoodsList(startCursor,
            of(0, request.getCount()));
        break;
      case CATEGORY:
        slice = goodsRepository.findAllByCategory(request.getCategory(), startCursor,
            of(0, request.getCount()));
        break;
        
      case KEYWORD:
        slice = goodsRepository.findAllByKeyword(request.getKeyword(), startCursor,
            of(0, request.getCount()));
        break;
        
      case CATEGORY_AND_KEYWORD:
        slice = goodsRepository.findAllByCategoryAndKeyword(request.getCategory(),
            request.getKeyword(), startCursor, of(0, request.getCount()));
        break;
      
      default:
        break;
    }
  
    if (slice != null && slice.hasContent()) {
      slice.forEach(list::add);
    }
  
    Response<GoodsInfo> response = new Response<>();
    if (list.size() >= request.getCount()) {
      Goods goods = list.get(list.size() - 1);
      String nextCursor = String.valueOf(goods.getModifiedAt().getTime());
      String nextRef = response.generateNextRef(request, nextCursor);
      response.setNextCursor(nextCursor);
      response.setNextRef(nextRef);
    } else {
      response.setNextCursor("");
      response.setNextRef("");
    }

    List<GoodsInfo> goodsInfoList = new ArrayList<>();
    for (Goods goods : list) {
      goodsInfoList.add(generateGoodsInfo(goods));
    }
    response.setContent(goodsInfoList);
    return response;
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
    GoodsInfo goodsInfo = new GoodsInfo(goods);

    // Set like ID if exist
    Long memberId = memberService.currentMemberId();
    if (memberId != null) {
      Optional<GoodsLike> optional = goodsLikeRepository
          .findByGoodsNoAndCreatedBy(goods.getGoodsNo(), memberId);
      if (optional.isPresent()) {
        goodsInfo.setLikeId(optional.get().getId());
      }
    }
    return goodsInfo;
  }
}