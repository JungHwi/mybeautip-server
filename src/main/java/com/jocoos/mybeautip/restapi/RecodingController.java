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
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.recoding.ViewRecoding;
import com.jocoos.mybeautip.recoding.ViewRecodingService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/recodings")
public class RecodingController {

  private final int DAY_IN_MS = 1000 * 60 * 60 * 24;

  private final ViewRecodingService viewRecodingService;
  private final PostRepository postRepository;
  private final GoodsRepository goodsRepository;

  public RecodingController(ViewRecodingService viewRecodingService,
                            PostRepository postRepository,
                            GoodsRepository goodsRepository) {
    this.viewRecodingService = viewRecodingService;
    this.postRepository = postRepository;
    this.goodsRepository = goodsRepository;
  }

  /**
   * Return viewed posts for 7 days or max 100 items.
   * @param count counts for
   * @return Slice<RecodingInfo></RecodingInfo>
   */
  @GetMapping
  @ResponseBody
  public CursorResponse  findAllViewedPosts(@RequestParam(defaultValue = "20") int count,
                                                             @RequestParam(required = false) String category,
                                                             @RequestParam(required = false) String cursor) {
    Slice<ViewRecoding> recodings = viewRecodingService.findByWeekAgo(count, cursor);
    List<RecodingInfo> result = Lists.newArrayList();

    recodings.stream().forEach(recoding -> result.add(getBasicInfo(recoding)));
    log.debug("{}", result);

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<RecodingInfo>("/api/1/recodings", result)
       .withCategory(category)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  private RecodingInfo getBasicInfo(ViewRecoding recoding) {
    switch (recoding.getCategory()) {
      case 1:
        return postRepository.findById(Long.parseLong(recoding.getItemId()))
                .map(post -> new RecodingInfo(recoding, post))
                .orElseGet(() -> new RecodingInfo(recoding));
      case 2:
        return goodsRepository.findById(recoding.getItemId())
           .map(goods -> new RecodingInfo(recoding, goods))
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

    public RecodingInfo(ViewRecoding viewRecoding, Post src) {
      BeanUtils.copyProperties(viewRecoding, this);
      if (src != null) {
        detail = new BasicInfo(src);
      }
    }

    public RecodingInfo(ViewRecoding viewRecoding, Goods src) {
      BeanUtils.copyProperties(viewRecoding, this);
      if (src != null) {
        detail = new BasicInfo(src);
      }
    }
  }

  @Data
  public static class BasicInfo {
    private Long id;
    private String goodsNo;
    private String title;
    private int category;
    private String thumbnailUrl;
    private Date createdAt;

    public BasicInfo(Post post) {
      BeanUtils.copyProperties(post, this);
    }

    public BasicInfo(Goods goods) {
      BeanUtils.copyProperties(goods, this);
      this.title = goods.getGoodsNm();
      this.thumbnailUrl = goods.getMainImageData().toString();
    }
  }
}
