package com.jocoos.mybeautip.restapi;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.godo.GoodsDetailService;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;


@Slf4j
@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final GoodsRepository goodsRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final GoodsDetailService goodsDetailService;

  public GoodsController(MemberService memberService,
                         GoodsService goodsService,
                         GoodsRepository goodsRepository,
                         GoodsLikeRepository goodsLikeRepository,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsDetailService goodsDetailService) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.goodsRepository = goodsRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsDetailService = goodsDetailService;
  }

  @GetMapping
  public CursorResponse getGoodsList(@Valid GoodsListRequest request) {
    return goodsService.getGoodsList(request);
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
  public CursorResponse getRelatedVideos(@PathVariable("goods_no") String goodsNo,
                                         @RequestParam(defaultValue = "50") int count,
                                         @RequestParam(required = false) String cursor,
                                         HttpServletRequest httpServletRequest,
                                         BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid request");
    }

    if (goodsNo.length() != 10) {
      throw new BadRequestException("invalid goods_no: " + goodsNo);
    }

   return getVideos(goodsNo, count, cursor, httpServletRequest.getRequestURI());
  }

  @GetMapping("/{goodsNo}/details")
  public ResponseEntity<String> getGoodsDetail(@PathVariable String goodsNo,
                                               @RequestParam(defaultValue = "false") boolean includeVideo) {
    return new ResponseEntity<>(goodsDetailService.getGoodsDetailPage(goodsNo, includeVideo), HttpStatus.OK);
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
          goods.setLikeCount(goods.getLikeCount() + 1);
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
    private GoodsInfo goods;

    GoodsLikeInfo(GoodsLike goodsLike) {
      BeanUtils.copyProperties(goodsLike, this);
      goods = new GoodsInfo(goodsLike.getGoods(), goodsLike.getId());
    }
  }

  private CursorResponse getVideos(String goodsNo, int count, String cursor, String requestUri) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<VideoGoods> slice = videoGoodsRepository.findByCreatedAtBeforeAndGoodsGoodsNo(startCursor, goodsNo, pageable);
    List<VideoGoodsController.VideoGoodsInfo> result = new ArrayList<>();
    VideoGoodsController.VideoGoodsInfo videoInfo;
    for (VideoGoods video : slice.getContent()) {
      videoInfo = new VideoGoodsController.VideoGoodsInfo(video,
        new MemberInfo(video.getMember(), memberService.getFollowingId(video.getMember())));
      result.add(videoInfo);
    }

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
      return new CursorResponse.Builder<>(requestUri, result)
        .withCount(count)
        .withCursor(nextCursor).toBuild();
    } else {
      return new CursorResponse.Builder<>(requestUri, result).toBuild();
    }
  }
}