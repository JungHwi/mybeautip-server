package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsListRequest;
import com.jocoos.mybeautip.goods.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {
  
  @Autowired
  private GoodsService goodsService;
  
  @GetMapping
  public ResponseEntity<? extends Object> getGoodsList(GoodsListRequest request) {
    if (request.getCount() == null) {
      request.setCount(20);  // FIXME: Use constant value
    }
    
    if (request.getCount() > 100) {  // FIXME: Use constant value
      request.setCount(100);
    }
    
    Response response = goodsService.getGoodsList(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  @GetMapping("/{goodsNo}")
  public ResponseEntity<Goods> getGoods(@PathVariable("goodsNo") String goodsNo) {
    Goods goods = goodsService.getGoods(goodsNo);
    return ResponseEntity.ok(goods);
  }
}