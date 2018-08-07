package com.jocoos.mybeautip.restapi;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.VideoGoodsService;


@Slf4j
@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoGoodsService videoGoodsService;
  private final GoodsRepository goodsRepository;
  private final GoodsLikeRepository goodsLikeRepository;


  public GoodsController(MemberService memberService,
                         GoodsService goodsService,
                         VideoGoodsService videoGoodsService,
                         GoodsRepository goodsRepository,
                         GoodsLikeRepository goodsLikeRepository) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.videoGoodsService = videoGoodsService;
    this.goodsRepository = goodsRepository;
    this.goodsLikeRepository = goodsLikeRepository;
  }

  @GetMapping
  public ResponseEntity<Response> getGoodsList(@Valid GoodsListRequest request) {
    Response response = goodsService.getGoodsList(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{goodsNo}")
  public GoodsInfo getGoods(@PathVariable("goodsNo") String goodsNo) {
    Optional<Goods> optional = goodsRepository.findByGoodsNo(goodsNo);
    if (optional.isPresent()) {
      return goodsService.generateGoodsInfo(optional.get());
    } else {
      throw new NotFoundException("goods_not_found", "goods not found: " + goodsNo);
    }
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

    Response response = videoGoodsService.getVideos(goodsNo, request, httpServletRequest.getRequestURI());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{goodsNo}/details")
  public ResponseEntity<?> getGoodsDetail(@PathVariable String goodsNo) {
    //TODO: Implementation for web view
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Transactional
  @PostMapping("/{goodsNo:.+}/likes")
  public ResponseEntity<GoodsLikeInfo> addGoodsLike(@PathVariable String goodsNo) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return goodsRepository.findByGoodsNo(goodsNo)
        .map(goods -> {
          if (goodsLikeRepository.findByGoodsGoodsNoAndCreatedBy(goodsNo, memberId).isPresent()) {
            throw new BadRequestException("duplicated_goods_like", "Already goods liked");
          }

          goodsRepository.updateLikeCount(goodsNo, 1);
          GoodsLike goodsLike = goodsLikeRepository.save(new GoodsLike(goods));
          return new ResponseEntity<>(new GoodsLikeInfo(goodsLike), HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("goods_not_found", "invalid goods no"));
  }

  @Transactional
  @DeleteMapping("/{goodsNo:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeGoodsLike(@PathVariable String goodsNo,
                                           @PathVariable Long likeId) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return goodsLikeRepository.findByIdAndGoodsGoodsNoAndCreatedBy(likeId, goodsNo, memberId)
        .map(goods -> {
          Optional<GoodsLike> liked = goodsLikeRepository.findById(likeId);
          if (!liked.isPresent()) {
            throw new NotFoundException("like_not_found", "invalid goods like id");
          }

          goodsLikeRepository.delete(liked.get());
          goodsRepository.updateLikeCount(goodsNo, -1);
          return new ResponseEntity(HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("goods_not_found", "invalid goods no or like id"));
  }

  @Data
  public static class GoodsLikeInfo {
    private Long id;
    private Long createdBy;
    private Date createdAt;
    private GoodsInfo goodsInfo;

    public GoodsLikeInfo(GoodsLike goodsLike) {
      BeanUtils.copyProperties(goodsLike, this);
      goodsInfo = new GoodsInfo(goodsLike.getGoods(), goodsLike.getId());
    }
  }
}