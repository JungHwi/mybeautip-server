package com.jocoos.mybeautip.restapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.recoding.ViewRecoding;
import com.jocoos.mybeautip.recoding.ViewRecodingService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me")
public class RecodingController {

    private static final String RESOURCE_TYPE_POST = "post";
    private static final String RESOURCE_TYPE_GOODS = "goods";
    private static final String RESOURCE_TYPE_VIDEO = "video";

    private final ViewRecodingService viewRecodingService;
    private final LegacyMemberService legacyMemberService;
//    private final PostService postService;
    private final GoodsService goodsService;
    private final MessageService messageService;
//    private final PostRepository postRepository;
    private final GoodsRepository goodsRepository;
    private final VideoRepository videoRepository;
//    private final PostLikeRepository postLikeRepository;
    private final GoodsLikeRepository goodsLikeRepository;
    private final VideoLikeRepository videoLikeRepository;

    public RecodingController(ViewRecodingService viewRecodingService,
                              LegacyMemberService legacyMemberService,
//                              PostService postService,
                              GoodsService goodsService,
                              MessageService messageService,
//                              PostRepository postRepository,
                              GoodsRepository goodsRepository,
                              VideoRepository videoRepository,
//                              PostLikeRepository postLikeRepository,
                              GoodsLikeRepository goodsLikeRepository,
                              VideoLikeRepository videoLikeRepository) {
        this.viewRecodingService = viewRecodingService;
        this.legacyMemberService = legacyMemberService;
//        this.postService = postService;
        this.goodsService = goodsService;
        this.messageService = messageService;
//        this.postRepository = postRepository;
        this.goodsRepository = goodsRepository;
        this.videoRepository = videoRepository;
//        this.postLikeRepository = postLikeRepository;
        this.goodsLikeRepository = goodsLikeRepository;
        this.videoLikeRepository = videoLikeRepository;
    }

    /**
     * Return viewed posts for 7 days or max 100 items.
     *
     * @param count counts for
     * @return Slice<RecodingInfo></RecodingInfo>
     */
    @GetMapping("/recodings")
    @ResponseBody
    public CursorResponse findAllMyViews(@RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) String cursor,
                                         @RequestParam(required = false) Integer category) {
        Long memberId = legacyMemberService.currentMemberId();

        Slice<ViewRecoding> recodings = viewRecodingService.findByWeekAgo(memberId, count, cursor, category);
        List<RecodingInfo> result = new ArrayList<>();

        recodings.stream().forEach(recoding -> result.add(getBasicInfo(recoding)));
        log.debug("{}", result);

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getModifiedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/me/recodings", result)
                .withCount(count)
                .withCategory(category)
                .withCursor(nextCursor).toBuild();
    }

    /**
     * Return viewed posts for 7 days or max 100 items.
     *
     * @param count counts for
     * @return Slice<ViewsLogInfo></ViewsLogInfo>
     */
    @GetMapping("/views/log")
    public CursorResponse findAllMyViewsLog(@RequestParam(defaultValue = "20") int count,
                                            @RequestParam(required = false) String cursor,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Long memberId = legacyMemberService.currentMemberId();

        Slice<ViewRecoding> recodings = viewRecodingService.findByWeekAgo(memberId, count, cursor, null);
        List<ViewsLogInfo> result = new ArrayList<>();

        recodings.forEach(recoding -> result.add(getViewsLogInfo(recoding, lang)));
        log.debug("{}", result);

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getModifiedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/me/views/log", result)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping("/views/goods")
    public CursorResponse findAllMyViewsLogForGoods(@RequestParam(defaultValue = "20") int count,
                                                    @RequestParam(required = false) String cursor,
                                                    @RequestParam(name = "broker", required = false) Long broker) {
        Long memberId = legacyMemberService.currentMemberId();

        Slice<ViewRecoding> recodings = viewRecodingService.findByWeekAgo(memberId, count, cursor, 2);
        List<GoodsInfo> result = new ArrayList<>();

        for (ViewRecoding recoding : recodings) {
            goodsService.generateGoodsInfo(recoding.getItemId(), TimeSaleCondition.createWithBroker(broker)).ifPresent(result::add);
        }

        log.debug("{}", result);

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getModifiedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/me/views/goods", result)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    private RecodingInfo getBasicInfo(ViewRecoding recoding) {
        Long me = legacyMemberService.currentMemberId();
        switch (recoding.getCategory()) {
//            case 1:
//                return postRepository.findById(Long.parseLong(recoding.getItemId()))
//                        .map(post -> {
//                            Long likeId = postLikeRepository.findByPostIdAndCreatedById(post.getId(), me)
//                                    .map(PostLike::getId).orElse(null);
//                            return new RecodingInfo(recoding, post, likeId);
//                        })
//                        .orElseGet(() -> new RecodingInfo(recoding));
            case 2:
                return goodsRepository.findByGoodsNo(recoding.getItemId())
                        .map(goods -> {
                            Long likeId = goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goods.getGoodsNo(), me)
                                    .map(GoodsLike::getId).orElse(null);
                            return new RecodingInfo(recoding, goods, likeId);
                        })
                        .orElseGet(() -> new RecodingInfo(recoding));

            case 3:
                return videoRepository.findById(Long.parseLong(recoding.getItemId()))
                        .map(video -> {
                            Long likeId = videoLikeRepository.findByVideoIdAndCreatedByIdAndStatus(video.getId(), me, LIKE)
                                    .map(VideoLike::getId).orElse(null);
                            return new RecodingInfo(recoding, video, likeId);
                        })
                        .orElseGet(() -> new RecodingInfo(recoding));

            default:
                throw new IllegalArgumentException("Unknown category type - " + recoding.getCategory());
        }
    }

    private ViewsLogInfo getViewsLogInfo(ViewRecoding recoding, String lang) {
        Long me = legacyMemberService.currentMemberId();
        switch (recoding.getCategory()) {
//            case 1:
//                return postRepository.findById(Long.parseLong(recoding.getItemId()))
//                        .map(post -> {
//                            Long likeId = postLikeRepository.findByPostIdAndCreatedById(post.getId(), me)
//                                    .map(PostLike::getId).orElse(null);
//                            String content = messageService.getMessage(postService.getPostCategoryName(post.getCategory()), lang);
//                            return new ViewsLogInfo(recoding, post, content, likeId);
//                        })
//                        .orElseGet(() -> new ViewsLogInfo(recoding, RESOURCE_TYPE_POST));
            case 2:
                return goodsRepository.findByGoodsNo(recoding.getItemId())
                        .map(goods -> {
                            Long likeId = goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goods.getGoodsNo(), me)
                                    .map(GoodsLike::getId).orElse(null);
                            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.KOREA);
                            String formattedGoodsPrice = format.format(goods.getGoodsPrice());
                            return new ViewsLogInfo(recoding, goods, formattedGoodsPrice, likeId);
                        })
                        .orElseGet(() -> new ViewsLogInfo(recoding, RESOURCE_TYPE_GOODS));
            case 3:
                return videoRepository.findById(Long.parseLong(recoding.getItemId()))
                        .map(video -> {
                            Long likeId = videoLikeRepository.findByVideoIdAndCreatedByIdAndStatus(video.getId(), me, LIKE)
                                    .map(VideoLike::getId).orElse(null);
                            return new ViewsLogInfo(recoding, video, likeId);
                        })
                        .orElseGet(() -> new ViewsLogInfo(recoding, RESOURCE_TYPE_VIDEO));
            default:
                throw new IllegalArgumentException("Unknown category type - " + recoding.getCategory());
        }
    }

    @NoArgsConstructor
    @Data
    public static class RecodingInfo {
        private Long id;
        private String itemId;
        private int category;
        private Date createdAt;
        @JsonIgnore
        private Date modifiedAt;
        private BasicInfo detail;

        public RecodingInfo(ViewRecoding viewRecoding) {
            BeanUtils.copyProperties(viewRecoding, this);
        }

//        public RecodingInfo(ViewRecoding viewRecoding, Post src, Long likeId) {
//            BeanUtils.copyProperties(viewRecoding, this);
//            if (src != null) {
//                detail = new BasicInfo(src, likeId);
//            }
//        }

        public RecodingInfo(ViewRecoding viewRecoding, Goods src, Long likeId) {
            BeanUtils.copyProperties(viewRecoding, this);
            if (src != null) {
                detail = new BasicInfo(src, likeId);
            }
        }

        public RecodingInfo(ViewRecoding viewRecoding, Video src, Long likeId) {
            BeanUtils.copyProperties(viewRecoding, this);
            if (src != null) {
                detail = new BasicInfo(src, likeId);
            }
        }
    }

    @Data
    public static class BasicInfo {
        private Long id;
        private String goodsNo;
        private String title;
        private Integer category;
        private String thumbnailUrl;
        private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
        private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
        private Integer goodsDiscount;  // 상품 할인 값
        private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
        private Integer goodsPrice;  // 판매가
        private Integer fixedPrice;  // 정가
        private Integer likeCount;
        private Long likeId;
        private String type;
        private Object state;
        private String username;
        private Date createdAt;

//        public BasicInfo(Post post, Long likeId) {
//            BeanUtils.copyProperties(post, this);
//            this.likeId = likeId;
//        }

        public BasicInfo(Goods goods, Long likeId) {
            BeanUtils.copyProperties(goods, this);
            this.state = goods.getState();
            this.title = goods.getGoodsNm();
            this.thumbnailUrl = goods.getMainImageData().toString();
            this.category = null;
            this.likeId = likeId;
        }

        public BasicInfo(Video video, Long likeId) {
            BeanUtils.copyProperties(video, this);
            this.category = null;
            this.state = video.getState();
            this.likeId = likeId;
            this.username = video.getMember().getUsername();
        }
    }

    @NoArgsConstructor
    @Data
    public static class ViewsLogInfo {
        private Long id;
        private String resourceType;  // "post", "video", "goods"
        private Long resourceId;
        private String imageUrl;
        private String title;
        private String content;
        private Long likeId;
        private Integer goodsState;
        private String videoState;
        private String videoVisibility;
        private Date videoDeletedAt;
        private Date modifiedAt;

        public ViewsLogInfo(ViewRecoding log, String resourceType) {
            BeanUtils.copyProperties(log, this);
            this.resourceType = resourceType;
            this.resourceId = 0L;
            this.imageUrl = "";
            this.title = "";
            this.content = "";
        }

//        public ViewsLogInfo(ViewRecoding log, Post post, String content, Long likeId) {
//            this.id = log.getId();
//            this.modifiedAt = log.getModifiedAt();
//            this.resourceType = RESOURCE_TYPE_POST;
//            this.resourceId = post.getId();
//            this.imageUrl = post.getThumbnailUrl();
//            this.title = post.getTitle();
//            this.content = content;
//            this.likeId = likeId;
//        }

        public ViewsLogInfo(ViewRecoding log, Goods goods, String formattedGoodsPrice, Long likeId) {
            this.id = log.getId();
            this.modifiedAt = log.getModifiedAt();
            this.resourceType = RESOURCE_TYPE_GOODS;
            this.resourceId = Long.parseLong(goods.getGoodsNo());
            this.imageUrl = goods.getListImageData().toString();
            this.title = goods.getGoodsNm();
            this.content = formattedGoodsPrice;
            this.likeId = likeId;
            this.goodsState = goods.getState();
        }

        public ViewsLogInfo(ViewRecoding log, Video video, Long likeId) {
            this.id = log.getId();
            this.modifiedAt = log.getModifiedAt();
            this.resourceType = RESOURCE_TYPE_VIDEO;
            this.resourceId = video.getId();
            this.imageUrl = video.getThumbnailUrl();
            this.title = video.getTitle();
            this.content = video.getMember().getUsername();
            this.likeId = likeId;
            this.videoState = video.getState();
            this.videoVisibility = video.getVisibility();
        }
    }
}
