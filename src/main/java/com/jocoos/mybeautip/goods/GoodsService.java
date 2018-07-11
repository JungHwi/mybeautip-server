package com.jocoos.mybeautip.goods;

import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
public class GoodsService {
  private final GoodsRepository goodsRepository;
  
  private enum FILTER {ALL, CATEGORY, KEYWORD, CATEGORY_AND_KEYWORD};
  
  public GoodsService(GoodsRepository goodsRepository) {
    this.goodsRepository = goodsRepository;
  }
  
  public Goods getGoods(String goodsNo) {
    Optional<Goods> optional = goodsRepository.findById(goodsNo);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RuntimeException("Not found");  // FIXME: Use defined exception
    }
  }
  
  public Response getGoodsList(GoodsListRequest request) {
    long startCursor;
    
    if (Strings.isBlank(request.getCursor())) {
      startCursor = System.currentTimeMillis();
    } else {
      startCursor = Long.parseLong(request.getCursor());
    }
    
    List<Goods> list = new ArrayList<>();
    Slice<Goods> slice = null;
    
    FILTER filter  = getRequestFilter(request);
    log.debug("filter: " + filter.toString());
    switch (filter) {
      case ALL:
        slice = goodsRepository.getGoodsList(startCursor,
            of(0, request.getCount() + 1));
        break;
      case CATEGORY:
        slice = goodsRepository.findAllByCategory(request.getCategory(), startCursor,
            of(0, request.getCount() + 1));
        break;
        
      case KEYWORD:
        slice = goodsRepository.findAllByKeyword(request.getKeyword(), startCursor,
            of(0, request.getCount() + 1));
        break;
        
      case CATEGORY_AND_KEYWORD:
        slice = goodsRepository.findAllByCategoryAndKeyword(request.getCategory(),
            request.getKeyword(), startCursor, of(0, request.getCount() + 1));
        break;
      
      default:
        break;
    }
  
    if (slice != null && slice.hasContent()) {
      slice.forEach(list::add);
    }
    
    Response response = new Response();
    if (list.size() >= request.getCount() + 1) {
      Goods goods = list.get(list.size() - 1);
      response.setNextCursor(goods.getUpdatedAt().toString());
      list.remove(goods);
    } else {
      response.setNextCursor("");
    }
    
    response.setGoods(list);
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
}