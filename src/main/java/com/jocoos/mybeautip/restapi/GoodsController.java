package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.godo.GoodsDetailService;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final GoodsOptionService goodsOptionService;
  private final GoodsRepository goodsRepository;
  private final GoodsOptionRepository goodsOptionRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final GoodsDetailService goodsDetailService;

  public GoodsController(MemberService memberService,
                         GoodsService goodsService,
                         VideoService videoService,
                         GoodsOptionService goodsOptionService,
                         GoodsRepository goodsRepository,
                         GoodsOptionRepository goodsOptionRepository,
                         GoodsLikeRepository goodsLikeRepository,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsDetailService goodsDetailService) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.videoService = videoService;
    this.goodsOptionService = goodsOptionService;
    this.goodsRepository = goodsRepository;
    this.goodsOptionRepository = goodsOptionRepository;
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

  @GetMapping("/{goods_no}/reviewers")
  public GoodsRelatedVideoInfoResponse getReviewers(@PathVariable("goods_no") String goodsNo) {
    GoodsRelatedVideoInfoResponse response = new GoodsRelatedVideoInfoResponse();
    return goodsRepository.findByGoodsNo(goodsNo)
      .map(goods -> {
        Page<Member> page = videoGoodsRepository.getDistinctMembers(goods, PageRequest.of(0, 6));
        if (page.hasContent()) {
          List<MemberInfo> result = new ArrayList<>();
          for (Member m : page.getContent()) {
            result.add(memberService.getMemberInfo(m));
          }
          response.setMembers(result);
          response.setTotalMemberCount(page.getTotalElements());
        }
        return response;
      })
      .orElseThrow(()-> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));
  }

  @GetMapping("/{goodsNo}/details")
  public ResponseEntity<String> getGoodsDetail(@PathVariable String goodsNo,
                                               @RequestParam(defaultValue = "false") boolean includeVideo) {
    return new ResponseEntity<>(goodsDetailService.getGoodsDetail(goodsNo, includeVideo), HttpStatus.OK);
  }

  @GetMapping("/{goodsNo}/related-goods")
  public ResponseEntity<List<GoodsInfo>> getRelatedGoods(@PathVariable String goodsNo) {
    List<Goods> relatedGoods = goodsService.getRelatedGoods(goodsNo);
    List<GoodsInfo> result = new ArrayList<>();
    for (Goods goods : relatedGoods) {
      result.add(goodsService.generateGoodsInfo(goods));
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // Will be deprecated
  @GetMapping("/{goodsNo}/options")
  public ResponseEntity<List<GoodsOptionInfo>> getGoodsOptions(@PathVariable Integer goodsNo) {
    Goods goods = goodsRepository.findByGoodsNo(String.valueOf(goodsNo))
        .orElseThrow(()-> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));
    
    List<GoodsOption> options = goodsOptionRepository.findByGoodsNo(goodsNo);
    List<GoodsOptionInfo> result = new ArrayList<>();
    
    for (GoodsOption option : options) {
      result.add(new GoodsOptionInfo(option, isSoldOut(goods, option)));
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
  
  @GetMapping("/{goodsNo}/option-data")
  public GoodsOptionService.GoodsOptionInfo getGoodsOptionData(@PathVariable Integer goodsNo) {
    return goodsOptionService.getGoodsOptionData(goodsNo);
  }
  
  private boolean isSoldOut(Goods goods, GoodsOption option) {
    if ("n".equals(option.getOptionSellFl())) { // 옵션 판매안함
      return true;
    }
    
    if ("y".equals(goods.getSoldOutFl())) { // 상품 품절 플래그
      return true;
    }
    
    if ("y".equals(goods.getStockFl()) && goods.getTotalStock() <= 0) { // 재고량에 따름, 총 재고량 부족
      return true;
    }
  
    // 재고량에 따름, 옵션 재고량 부족
    return "y".equals(goods.getStockFl()) && option.getStockCnt() <= 0;
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
          if (goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goodsNo, memberId).isPresent()) {
            throw new BadRequestException("duplicated_goods_like", "Already goods liked");
          }

          goodsRepository.updateLikeCount(goodsNo, 1);
          goods.setLikeCount(goods.getLikeCount() + 1);
          GoodsLike goodsLike = goodsLikeRepository.save(new GoodsLike(goods));
          GoodsLikeInfo info = new GoodsLikeInfo(goodsLike, goodsService.generateGoodsInfo(goods));
          return new ResponseEntity<>(info, HttpStatus.OK);
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

    return goodsLikeRepository.findByIdAndGoodsGoodsNoAndCreatedById(likeId, goodsNo, memberId)
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

    GoodsLikeInfo(GoodsLike goodsLike, GoodsInfo goods) {
      BeanUtils.copyProperties(goodsLike, this);
      this.goods = goods;
    }
  }

  @Data
  public static class GoodsOptionInfo {
    private Integer optionNo;
    private String optionValue;
    private String optionValue1;
    private String optionValue2;
    private Integer optionPrice;
    private Integer stockCnt;
    private Boolean soldOut;

    GoodsOptionInfo(GoodsOption option, boolean soldOut) {
      BeanUtils.copyProperties(option, this);
      this.optionValue = option.getOptionValue1();
      this.soldOut = soldOut;
    }
  }

  private CursorResponse getVideos(String goodsNo, int count, String cursor, String requestUri) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<VideoGoods> slice = videoGoodsRepository.findByCreatedAtBeforeAndGoodsGoodsNo(startCursor, goodsNo, pageable);
    List<VideoController.VideoInfo> result = new ArrayList<>();

    for (VideoGoods videoGoods : slice.getContent()) {
      result.add(videoService.generateVideoInfo(videoGoods.getVideo()));
    }

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }
    return new CursorResponse.Builder<>(requestUri, result)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  @Data
  @AllArgsConstructor
  class GoodsRelatedVideoInfoResponse {
    Long totalMemberCount;
    List<MemberInfo> members;

    GoodsRelatedVideoInfoResponse() {
      totalMemberCount = 0L;
    }
  }
}