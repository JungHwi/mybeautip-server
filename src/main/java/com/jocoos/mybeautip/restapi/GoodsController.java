package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.godo.GoodsDetailService;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.search.KeywordService;
import com.jocoos.mybeautip.video.LegacyVideoService;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.springframework.data.domain.PageRequest.of;


@Slf4j
@RestController
@RequestMapping("/api/1/goods")
public class GoodsController {

    private static final String GOODS_NOT_FOUND = "goods.not_found";
    private static final String ALREADY_LIKED = "like.already_liked";
    private static final String LIKE_NOT_FOUND = "like.not_found";
    private static final String HASHTAG_SIGN = "#";
    private static final int MAX_REVIEWER_COUNT = 6;
    private static final List<String> validSort
            = Arrays.asList("like", "order", "hit", "review", "high-price", "low-price", "latest");
    private final LegacyMemberService legacyMemberService;
    private final GoodsService goodsService;
    private final LegacyVideoService legacyVideoService;
    private final GoodsOptionService goodsOptionService;
    private final MessageService messageService;
    private final GoodsRepository goodsRepository;
    private final GoodsLikeRepository goodsLikeRepository;
    private final VideoGoodsRepository videoGoodsRepository;
    private final GoodsDetailService goodsDetailService;
    private final KeywordService keywordService;

    public GoodsController(LegacyMemberService legacyMemberService,
                           GoodsService goodsService,
                           LegacyVideoService legacyVideoService,
                           GoodsOptionService goodsOptionService,
                           MessageService messageService,
                           GoodsRepository goodsRepository,
                           GoodsLikeRepository goodsLikeRepository,
                           VideoGoodsRepository videoGoodsRepository,
                           GoodsDetailService goodsDetailService,
                           KeywordService keywordService) {
        this.legacyMemberService = legacyMemberService;
        this.goodsService = goodsService;
        this.legacyVideoService = legacyVideoService;
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
                                       @RequestParam(required = false) String category,
                                       @RequestParam(name = "broker", required = false) Long broker) {
        if (count > 100) {
            count = 100;
        }
        if (keyword != null && keyword.length() > 255) {
            throw new BadRequestException("Valid keyword size is between 1 to 255.");
        }
        if (category != null && category.length() > 6) {
            throw new BadRequestException("Valid category size is between 1 to 6.");
        }
        if (sort != null && !validSort.contains(sort)) {
            throw new BadRequestException("Valid sort value is one of 'like', 'order', 'hit', 'review', 'high-price', 'low-price' or 'latest'.");
        }
        if (cursor != null && sort != null && !"latest".equals(sort)) {
            if (cursor > Integer.MAX_VALUE) {
                throw new BadRequestException("Invalid cursor");
            }
        }

        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.trim();
            if (keyword.startsWith(HASHTAG_SIGN)) {
                keyword = keyword.substring(HASHTAG_SIGN.length());
            }

            try {
                keywordService.updateKeywordCount(keyword);
                keywordService.logHistory(keyword, KeywordService.KeywordCategory.GOODS, legacyMemberService.currentMember());
            } catch (ConcurrencyFailureException e) { // Ignore
                log.warn("getGoods throws ConcurrencyFailureException: " + keyword);
            }
        }

        Slice<Goods> slice;
        if (StringUtils.isNotEmpty(keyword)) {
            category = sort = null;
            if (cursor == null) {
                cursor = 0L;
            }
            slice = goodsService.findAllByKeyword(keyword, cursor, count);
        } else {
            slice = goodsService.getGoodsList(count, cursor, sort, category);
        }

        List<GoodsInfo> result = new ArrayList<>();
        for (Goods goods : slice.getContent()) {
            result.add(goodsService.generateGoodsInfo(goods, TimeSaleCondition.createWithBroker(broker)));
        }

        String nextCursor = null;
        if (sort == null) {
            if (result.size() > 0) {
                nextCursor = String.valueOf(cursor.intValue() + 1);
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
                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
                              @RequestParam(name = "broker", required = false) Long broker) {
        Optional<Goods> optional = goodsRepository.findByGoodsNo(goodsNo);
        if (optional.isPresent()) {
            return goodsService.generateGoodsInfo(optional.get(), TimeSaleCondition.createWithBroker(broker));
        } else {
            throw new NotFoundException(messageService.getMessage(GOODS_NOT_FOUND, lang));
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
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
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
                        result.add(legacyMemberService.getMemberInfo(m));
                        count++;
                        if (count >= MAX_REVIEWER_COUNT) {
                            break;
                        }
                    }
                    response.setMembers(result);
                    response.setTotalMemberCount(memberSet.size());
                    return response;
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(GOODS_NOT_FOUND, lang)));
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
                                                                 @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
                                                                 @RequestParam(name = "broker", required = false) Long broker) {
        return goodsOptionService.getGoodsOptionData(goodsNo, lang, TimeSaleCondition.createWithBroker(broker));
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

    @PostMapping("/{goodsNo:.+}/likes")
    public ResponseEntity<GoodsLikeInfo> addGoodsLike(@PathVariable String goodsNo,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
                                                      @RequestParam(name = "broker", required = false) Long broker) {
        Long memberId = legacyMemberService.currentMemberId();
        return goodsRepository.findByGoodsNo(goodsNo)
                .map(goods -> {
                    if (goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goodsNo, memberId).isPresent()) {
                        throw new BadRequestException(messageService.getMessage(ALREADY_LIKED, lang));
                    }
                    GoodsLike goodsLike = goodsService.addLike(goods);
                    GoodsLikeInfo info = new GoodsLikeInfo(goodsLike, goodsService.generateGoodsInfo(goods, TimeSaleCondition.createWithBroker(broker)));
                    return new ResponseEntity<>(info, HttpStatus.OK);
                })
                .orElseThrow(() -> new NotFoundException(ErrorCode.GOODS_NOT_FOUND, messageService.getMessage(GOODS_NOT_FOUND, lang)));
    }

    @DeleteMapping("/{goodsNo:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeGoodsLike(@PathVariable String goodsNo,
                                             @PathVariable Long likeId,
                                             @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        GoodsLike like = goodsLikeRepository.findByIdAndGoodsGoodsNoAndCreatedById(likeId, goodsNo, legacyMemberService.currentMemberId())
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(LIKE_NOT_FOUND, lang)));
        goodsService.removeLike(like);
        return new ResponseEntity(HttpStatus.OK);
    }

    private CursorResponse getVideos(String goodsNo, int count, String cursor, String requestUri) {
        Date startCursor = (Strings.isBlank(cursor)) ?
                new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

        PageRequest pageable = of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<VideoGoods> slice = videoGoodsRepository.findByCreatedAtBeforeAndGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(
                startCursor, goodsNo, "PUBLIC", "CREATED", pageable);
        List<LegacyVideoController.VideoInfo> result = new ArrayList<>();

        for (VideoGoods videoGoods : slice.getContent()) {
            result.add(legacyVideoService.generateVideoInfo(videoGoods.getVideo()));
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