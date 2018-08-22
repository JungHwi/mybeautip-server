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
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.godo.GoodsDetailService;
import com.jocoos.mybeautip.goods.*;
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
                                         HttpServletRequest httpServletRequest) {
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

  @GetMapping("/{goodsNo}/related-goods")
  public ResponseEntity<List<GoodsInfo>> getRelatedGoods(@PathVariable String goodsNo) {
    PageRequest pageable = PageRequest.of(0, 5, new Sort(Sort.Direction.DESC, "id"));
    List<Goods> relatedGoods = goodsService.getRelatedGoods(goodsNo);
    List<GoodsInfo> result = new ArrayList<>();
    for (Goods goods : relatedGoods) {
      result.add(goodsService.generateGoodsInfo(goods));
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/{goodsNo}/options")
  public ResponseEntity<List<GoodsOption>> getGoodsOptions(@PathVariable Integer goodsNo) {
    List<GoodsOption> options = goodsService.getAllGoodsOptions(goodsNo);
    return new ResponseEntity<>(options, HttpStatus.OK);
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
    List<VideoController.VideoInfo> result = new ArrayList<>();

    for (VideoGoods videoGoods : slice.getContent()) {
      result.add(new VideoController.VideoInfo(videoGoods.getVideo(),
        memberService.getMemberInfo(videoGoods.getVideo().getMember())));
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