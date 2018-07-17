package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1/goods")
@Slf4j
public class GoodsController {

  private final GoodsService goodsService;
  private final GoodsRepository goodsRepository;

  public GoodsController(GoodsService goodsService, GoodsRepository goodsRepository) {
    this.goodsService = goodsService;
    this.goodsRepository = goodsRepository;
  }

  @GetMapping
  public ResponseEntity<Response> getGoodsList(@Valid GoodsListRequest request) {
    Response response = goodsService.getGoodsList(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{goodsNo}")
  public GoodsInfo getGoods(@PathVariable("goodsNo") String goodsNo) {
    return goodsRepository.findById(goodsNo)
        .map(GoodsInfo::new)
        .orElseThrow(() -> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));
  }
}