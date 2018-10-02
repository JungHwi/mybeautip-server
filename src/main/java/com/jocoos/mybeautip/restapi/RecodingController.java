package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsLike;
import com.jocoos.mybeautip.goods.GoodsLikeRepository;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.recoding.ViewRecoding;
import com.jocoos.mybeautip.recoding.ViewRecodingService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/recodings")
public class RecodingController {

  private final int DAY_IN_MS = 1000 * 60 * 60 * 24;

  private final ViewRecodingService viewRecodingService;
  private final MemberService memberService;
  private final PostRepository postRepository;
  private final GoodsRepository goodsRepository;
  private final VideoRepository videoRepository;
  private final PostLikeRepository postLikeRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final VideoLikeRepository videoLikeRepository;

  public RecodingController(ViewRecodingService viewRecodingService,
                            MemberService memberService,
                            PostRepository postRepository,
                            GoodsRepository goodsRepository,
                            VideoRepository videoRepository,
                            PostLikeRepository postLikeRepository,
                            GoodsLikeRepository goodsLikeRepository,
                            VideoLikeRepository videoLikeRepository) {
    this.viewRecodingService = viewRecodingService;
    this.memberService = memberService;
    this.postRepository = postRepository;
    this.goodsRepository = goodsRepository;
    this.videoRepository = videoRepository;
    this.postLikeRepository = postLikeRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.videoLikeRepository = videoLikeRepository;
  }

  /**
   * Return viewed posts for 7 days or max 100 items.
   * @param count counts for
   * @return Slice<RecodingInfo></RecodingInfo>
   */
  @GetMapping
  @ResponseBody
  public CursorResponse findAllMyViews(@RequestParam(defaultValue = "20") int count,
                                       @RequestParam(required = false) String cursor,
                                       @RequestParam(required = false) Integer category) {
    Long memberId = memberService.currentMemberId();

    Slice<ViewRecoding> recodings = viewRecodingService.findByWeekAgo(memberId, count, cursor, category);
    List<RecodingInfo> result = Lists.newArrayList();

    recodings.stream().forEach(recoding -> result.add(getBasicInfo(recoding)));
    log.debug("{}", result);

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<RecodingInfo>("/api/1/members/me/recodings", result)
       .withCount(count)
       .withCategory(category)
       .withCursor(nextCursor).toBuild();
  }

  private RecodingInfo getBasicInfo(ViewRecoding recoding) {
    Long me = memberService.currentMemberId();
    switch (recoding.getCategory()) {
      case 1:
        return postRepository.findById(Long.parseLong(recoding.getItemId()))
                .map(post -> {
                  Long likeId = postLikeRepository.findByPostIdAndCreatedById(post.getId(), me)
                    .map(PostLike::getId).orElse(null);
                  return new RecodingInfo(recoding, post, likeId);
                })
                .orElseGet(() -> new RecodingInfo(recoding));
      case 2:
        return goodsRepository.findById(recoding.getItemId())
           .map(goods -> {
             Long likeId = goodsLikeRepository.findByGoodsGoodsNoAndCreatedById(goods.getGoodsNo(), me)
               .map(GoodsLike::getId).orElse(null);
             return new RecodingInfo(recoding, goods, likeId);
           })
           .orElseGet(() -> new RecodingInfo(recoding));

      case 3:
        return videoRepository.findById(Long.parseLong(recoding.getItemId()))
          .map(video -> {
            Long likeId = videoLikeRepository.findByVideoIdAndCreatedById(video.getId(), me)
              .map(VideoLike::getId).orElse(null);
            return new RecodingInfo(recoding, video, likeId);
          })
          .orElseGet(() -> new RecodingInfo(recoding));

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
    private BasicInfo detail;

    public RecodingInfo(ViewRecoding viewRecoding) {
      BeanUtils.copyProperties(viewRecoding, this);
    }

    public RecodingInfo(ViewRecoding viewRecoding, Post src, Long likeId) {
      BeanUtils.copyProperties(viewRecoding, this);
      if (src != null) {
        detail = new BasicInfo(src, likeId);
      }
    }

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
    private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
    private Integer goodsDiscount;  // 상품 할인 값
    private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
    private Integer goodsPrice;  // 판매가
    private Integer fixedPrice;  // 정가
    private Integer likeCount;
    private Long likeId;
    private String type;
    private String state;
    private Date createdAt;

    public BasicInfo(Post post, Long likeId) {
      BeanUtils.copyProperties(post, this);
      this.likeId = likeId;
    }

    public BasicInfo(Goods goods, Long likeId) {
      BeanUtils.copyProperties(goods, this);
      this.title = goods.getGoodsNm();
      this.thumbnailUrl = goods.getMainImageData().toString();
      this.category = null;
      this.likeId = likeId;
    }

    public BasicInfo(Video video, Long likeId) {
      BeanUtils.copyProperties(video, this);
      this.category = null;
      this.likeId = likeId;
    }
  }
}
