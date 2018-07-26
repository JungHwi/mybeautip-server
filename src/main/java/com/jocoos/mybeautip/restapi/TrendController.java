package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.post.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/trends", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrendController {

  private final TrendRepository trendRepository;

  private final PostRepository postRepository;

  private final PostLikeRepository postLikeRepository;

  private final GoodsRepository goodsRepository;

  private final MemberService memberService;

  public TrendController(TrendRepository trendRepository,
                         PostRepository postRepository,
                         PostLikeRepository postLikeRepository,
                         GoodsRepository goodsRepository,
                         MemberService memberService) {
    this.postRepository = postRepository;
    this.trendRepository = trendRepository;
    this.postLikeRepository = postLikeRepository;
    this.goodsRepository = goodsRepository;
    this.memberService = memberService;
  }

  @GetMapping
  public ResponseEntity<List<TrendInfo>> getTrends(@RequestParam(defaultValue = "5") int count) {
    Long memberId = memberService.currentMemberId();
    Slice<Trend> trends = trendRepository.findAll(PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));
    List<TrendInfo> result = Lists.newArrayList();

    trends.stream().forEach(t -> {
      TrendInfo trendInfo = new TrendInfo();
      BeanUtils.copyProperties(t.getPost(), trendInfo);
      log.debug("trend info: {}", trendInfo);

      postLikeRepository.findByPostIdAndCreatedBy(t.getPost().getId(), memberId)
         .ifPresent(like -> trendInfo.setLikeId(like.getId()));

      result.add(trendInfo);
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/{id:.+}/goods")
  public ResponseEntity<List<GoodsInfo>> getGoods(@PathVariable Long id) {
    return trendRepository.findById(id)
       .map(trend -> {
         List<GoodsInfo> result = Lists.newArrayList();
         trend.getPost().getGoods().stream().forEach(gno -> {
           goodsRepository.findById(gno).ifPresent(g -> {
             result.add(new GoodsInfo(g));
           });
         });
         return new ResponseEntity<>(result, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addViewCount(@PathVariable Long id) {

    // TODO: Add history using spring AOP!!
    return trendRepository.findById(id)
       .map(trend -> {
         postRepository.updateViewCount(trend.getPost().getId(), 1L);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  @Transactional
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<PostLikeInfo> addTrendLike(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return trendRepository.findById(id)
       .map(trend -> {
         Long postId = trend.getPost().getId();
         if (postLikeRepository.findByPostIdAndCreatedBy(postId, memberId).isPresent()) {
           throw new BadRequestException("duplicated_post_like", "Already post liked");
         }

         PostLike postLike = postLikeRepository.save(new PostLike(postId));
         return new ResponseEntity<>(new PostLikeInfo(postLike), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  @Transactional
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeTrendLike(@PathVariable Long id,
                                           @PathVariable Long likeId){
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return trendRepository.findById(id)
       .map(trend -> postLikeRepository.findById(likeId).map(like -> {
         postLikeRepository.delete(like);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("like_not_found", "invalid post like id")))
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  /**
   * @see com.jocoos.mybeautip.post.Post
   */
  @Data
  public static class TrendInfo {
    private Long id;
    private String title;
    private String bannerText;
    private String description;
    private String thumbnailUrl;
    private int category;
    private Set<PostContent> contents;
    private List<String> goods;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private Date createdAt;
    private Long likeId;
  }

  @Data
  public static class PostLikeInfo {
    private Long id;
    private Date createdAt;

    public PostLikeInfo(PostLike postLike) {
      BeanUtils.copyProperties(postLike, this);
    }
  }
}
