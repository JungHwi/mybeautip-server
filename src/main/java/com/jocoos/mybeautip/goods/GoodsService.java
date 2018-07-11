package com.jocoos.mybeautip.goods;

import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
public class GoodsService {
  private final GoodsRepository goodsRepository;
  
  @Autowired
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
    if (StringUtils.isEmpty(request.getCursor())) {
      startCursor = System.currentTimeMillis();
    } else {
      startCursor = Long.parseLong(request.getCursor());
    }
    
    List<Goods> list = new ArrayList<>();
    
    if (!StringUtils.isEmpty(request.getCategory())) {
      log.debug("filter by category");
      Slice<Goods> slice = goodsRepository.findAllByCategory(request.getCategory(), startCursor,
          of(0, request.getCount() + 1));
      if (slice.hasContent()) {
        slice.forEach(list::add);
      }
    }
    
    if (StringUtils.isEmpty(request.getCategory())) {
      log.debug("no filter");
      Slice<Goods> slice = goodsRepository.getGoodsList(startCursor, of(0, request.getCount() + 1));
      if (slice.hasContent()) {
        slice.forEach(list::add);
      }
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
}