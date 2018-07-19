package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsListRequest;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {

  private final GoodsService goodsService;
  private final VideoService videoService;
  private final GoodsRepository goodsRepository;

  public GoodsController(GoodsService goodsService,
                         VideoService videoService,
                         GoodsRepository goodsRepository) {
    this.goodsService = goodsService;
    this.videoService = videoService;
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

  @GetMapping("/{goods_no}/videos")
  public ResponseEntity<Response> getRelatedVideos(@PathVariable("goods_no") String goodsNo,
                                              @Valid CursorRequest request,
                                              HttpServletRequest httpServletRequest,
                                              BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid request");
    }

    if (goodsNo.length() != 10) {
      throw new BadRequestException("invalid goods_no: " + goodsNo);
    }

    Response response = videoService.getVideos(goodsNo, request, httpServletRequest.getRequestURI());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}