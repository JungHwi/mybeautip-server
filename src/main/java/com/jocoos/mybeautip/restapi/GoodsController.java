package com.jocoos.mybeautip.restapi;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import static org.springframework.data.domain.PageRequest.of;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.godo.GoodsDetailService;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsLike;
import com.jocoos.mybeautip.goods.GoodsLikeRepository;
import com.jocoos.mybeautip.goods.GoodsOption;
import com.jocoos.mybeautip.goods.GoodsOptionService;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.search.KeywordService;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoService;


@Slf4j
@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final GoodsOptionService goodsOptionService;
  private final MessageService messageService;
  private final GoodsRepository goodsRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final GoodsDetailService goodsDetailService;
  private final KeywordService keywordService;

  private static final String GOODS_NOT_FOUND = "goods.not_found";
  private static final String ALREADY_LIKED = "like.already_liked";
  private static final String LIKE_NOT_FOUND = "like.not_found";
  
  private static final String HASHTAG_SIGN = "#";
  private static final int MAX_REVIEWER_COUNT = 6;
  private static final List<String> validSort
      = Arrays.asList("like", "order", "hit", "review", "high-price", "low-price", "latest");

  public GoodsController(MemberService memberService,
                         GoodsService goodsService,
                         VideoService videoService,
                         GoodsOptionService goodsOptionService,
                         MessageService messageService,
                         GoodsRepository goodsRepository,
                         GoodsLikeRepository goodsLikeRepository,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsDetailService goodsDetailService,
                         KeywordService keywordService) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.videoService = videoService;
    this.goodsOptionService = goodsOptionService;
    this.messageService = messageService;
    this.goodsRepository = goodsRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsDetailService = goodsDetailService;
    this.keywordService = keywordService;
  }
  
  @GetMapping
  public CursorResponse getGoodsList(@RequestParam(defaultValue = "20") int count,
                                     @RequestParam(required = false, defaultValue = "order") String sort,
                                     @RequestParam(required = false) Long cursor,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String category) {
    if (count > 100) {
      count = 100;
    }
    if (keyword != null && keyword.length() > 255) {
      throw new BadRequestException("invalid_keyword", "Valid keyword size is between 1 to 255.");
    }
    if (category != null && category.length() > 6) {
      throw new BadRequestException("invalid_category", "Valid category size is between 1 to 6.");
    }
    if (sort != null && !validSort.contains(sort)) {
      throw new BadRequestException("invalid_sort", "Valid sort value is one of 'like', 'order', 'hit', 'review', 'high-price', 'low-price' or 'latest'.");
    }
    if (cursor!= null && sort != null && !"latest".equals(sort)) {
      if (cursor > Integer.MAX_VALUE) {
        throw new BadRequestException("invalid_cursor", "Invalid cursor");
      }
    }

    if (StringUtils.isNotBlank(keyword)) {
      keyword = keyword.trim();
      if (keyword.startsWith(HASHTAG_SIGN)) {
        keyword = keyword.substring(HASHTAG_SIGN.length());
      }
      
      try {
        keywordService.updateKeywordCount(keyword);
        keywordService.logHistory(keyword, KeywordService.KeywordCategory.GOODS, memberService.currentMember());
      } catch (ConcurrencyFailureException e) { // Ignore
        log.warn("getGoods throws ConcurrencyFailureException: " + keyword);
      }
    }
    
    Slice<Goods> slice;
    if (StringUtils.isNotEmpty(keyword)) {
      category = sort = null;
      Date startCursor = (cursor == null) ? new Date() : new Date(cursor);
      slice = goodsRepository.findAllByKeyword(keyword, startCursor, of(0, count));
    } else {
      slice = goodsService.getGoodsList(count, cursor, sort, category);
    }
  
    List<GoodsInfo> result = new ArrayList<>();
    for (Goods goods : slice.getContent()) {
      result.add(goodsService.generateGoodsInfo(goods));
    }
  
    String nextCursor = null;
    if (sort == null) {
      if (result.size() > 0) {
        nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
      }
    } else {
      switch (sort) {
        case "like":
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getLikeCount());
          }
          break;
        case "order":
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getOrderCnt());
          }
          break;
        case "hit":
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getHitCnt());
          }
          break;
        case "review":
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getReviewCnt());
          }
          break;
        case "high-price":
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getGoodsPrice());
          }
          break;
        case "low-price":
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getGoodsPrice());
          }
          break;
        case "latest":
        default:
          if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
          }
          break;
      }
    }
  
    return new CursorResponse.Builder<>("/api/1/goods", result)
        .withCount(count)
        .withSort(sort)
        .withCursor(nextCursor)
        .withCategory(category)
        .withKeyword(keyword).toBuild();
  }

  @GetMapping("/{goodsNo}")
  public GoodsInfo getGoods(@PathVariable("goodsNo") String goodsNo,
                            @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Optional<Goods> optional = goodsRepository.findByGoodsNo(goodsNo);
    if (optional.isPresent()) {
      return goodsService.generateGoodsInfo(optional.get());
    } else {
      throw new NotFoundException("goods_not_found", messageService.getMessage(GOODS_NOT_FOUND, lang));
    }
  }

  @GetMapping("/{goods_no}/videos")
  public CursorResponse getRelatedVideos(@PathVariable("goods_no") String goodsNo,
                                         @RequestParam(defaultValue = "50") int count,
                                         @RequestParam(required = false) String cursor,
                                         HttpServletRequest httpServletRequest) {
   return getVideos(goodsNo, count, cursor, httpServletRequest.getRequestURI());
  }

  @GetMapping("/{goods_no}/reviewers")
  public GoodsRelatedVideoInfoResponse getReviewers(@PathVariable("goods_no") String goodsNo,
                                                    @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    GoodsRelatedVideoInfoResponse response = new GoodsRelatedVideoInfoResponse();
    return goodsRepository.findByGoodsNo(goodsNo)
      .map(goods -> {
        List<VideoGoods> videoGoodsList = videoGoodsRepository.findByGoodsAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(goods, "PUBLIC", "CREATED");
        
        // Get distinct members
        Set<Member> memberSet = new HashSet<>();
        int count = 0;
        for (VideoGoods videoGoods : videoGoodsList) {
          memberSet.add(videoGoods.getVideo().getMember());
        }
        
        List<MemberInfo> result = new ArrayList<>();
        for (Member m : memberSet) {
          result.add(memberService.getMemberInfo(m));
          count++;
          if (count >= MAX_REVIEWER_COUNT) {
            break;
          }
        }
        response.setMembers(result);
        response.setTotalMemberCount(memberSet.size());
        return response;
      })
      .orElseThrow(()-> new NotFoundException("goods_not_found", messageService.getMessage(GOODS_NOT_FOUND, lang)));
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
  
  @GetMapping("/{goodsNo}/option_data")
  public GoodsOptionService.GoodsOptionInfo getGoodsOptionData(@PathVariable Integer goodsNo,
                                                               @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return goodsOptionService.getGoodsOptionData(goodsNo, lang);
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
  public ResponseEntity<GoodsLikeInfo> addGoodsLike(@PathVariable String goodsNo,
                                                    @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    return goodsRepository.findByGoodsNo(goodsNo)
        .map(goods -> {
          if (goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goodsNo, memberId).isPresent()) {
            throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
          }
          GoodsLike goodsLike = goodsService.addLike(goods);
          GoodsLikeInfo info = new GoodsLikeInfo(goodsLike, goodsService.generateGoodsInfo(goods));
          return new ResponseEntity<>(info, HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("goods_not_found", messageService.getMessage(GOODS_NOT_FOUND, lang)));
  }

  @Transactional
  @DeleteMapping("/{goodsNo:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeGoodsLike(@PathVariable String goodsNo,
                                           @PathVariable Long likeId,
                                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    GoodsLike like = goodsLikeRepository.findByIdAndGoodsGoodsNoAndCreatedById(likeId, goodsNo, memberService.currentMemberId())
        .orElseThrow(() -> new NotFoundException("like_not_found", messageService.getMessage(LIKE_NOT_FOUND, lang)));
    goodsService.removeLike(like);
    return new ResponseEntity(HttpStatus.OK);
  }

  @Data
  public static class GoodsLikeInfo {
    private Long id;
    @Deprecated
    private Long createdBy;
    private Date createdAt;
    private GoodsInfo goods;

    GoodsLikeInfo(GoodsLike goodsLike, GoodsInfo goods) {
      BeanUtils.copyProperties(goodsLike, this);
      this.goods = goods;
    }
  }

  private CursorResponse getVideos(String goodsNo, int count, String cursor, String requestUri) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<VideoGoods> slice = videoGoodsRepository.findByCreatedAtBeforeAndGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(
        startCursor, goodsNo, "PUBLIC", "CREATED", pageable);
    List<VideoController.VideoInfo> result = new ArrayList<>();

    for (VideoGoods videoGoods : slice.getContent()) {
      result.add(videoService.generateVideoInfo(videoGoods.getVideo()));
    }

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(slice.getContent().get(slice.getContent().size() - 1).getCreatedAt().getTime());
    }
    return new CursorResponse.Builder<>(requestUri, result)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  @Data
  @AllArgsConstructor
  class GoodsRelatedVideoInfoResponse {
    Integer totalMemberCount;
    List<MemberInfo> members;

    GoodsRelatedVideoInfoResponse() {
      totalMemberCount = 0;
    }
  }
}