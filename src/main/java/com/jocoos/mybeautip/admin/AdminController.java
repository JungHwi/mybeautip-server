package com.jocoos.mybeautip.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;
import com.jocoos.mybeautip.recommendation.GoodsRecommendationRepository;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import com.jocoos.mybeautip.recommendation.MemberRecommendationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.post.Trend;
import com.jocoos.mybeautip.post.TrendRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminController {

  private final PostRepository postRepository;
  private final TrendRepository trendRepository;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;

  public AdminController(PostRepository postRepository,
                         TrendRepository trendRepository,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         MemberRecommendationRepository memberRecommendationRepository,
                         GoodsRecommendationRepository goodsRecommendationRepository) {
    this.postRepository = postRepository;
    this.trendRepository = trendRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
  }

  @DeleteMapping("/posts/{id:.+}")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
    postRepository.findById(id).map(post -> {
      post.setDeletedAt(new Date());
      postRepository.save(post);
      return Optional.empty();
    }).orElseThrow(() -> new NotFoundException("post_not_found", "post not found"));

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/trends")
  public ResponseEntity<TrendInfo> createTrend(@RequestBody CreateTrendRequest request) throws ParseException {
    log.debug("request: {}", request);

    Trend trend = new Trend();
    BeanUtils.copyProperties(request, trend);
    log.debug("trend: {}", trend);

    return postRepository.findById(request.getPostId()).map(p -> {
      trend.setPost(p);

      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
      try {
        trend.setStartedAt(df.parse(request.getStartedAt()));
        trend.setEndedAt(df.parse(request.getEndedAt()));
      } catch (ParseException e) {
        log.error("invalid date format", e);
      }

      trendRepository.save(trend);

      TrendInfo info = new TrendInfo();
      BeanUtils.copyProperties(trend, info);
      return new ResponseEntity<>(info, HttpStatus.OK);
    })
    .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @PostMapping("/members")
  public ResponseEntity<RecommendedMemberInfo> createRecommendedMember(
      @RequestBody CreateRecommendedMemberRequest request) throws ParseException {
    log.debug("request: {}", request);
    MemberRecommendation recommendation;
    Optional<MemberRecommendation> optional
        = memberRecommendationRepository.findById(request.getMemberId());
    if (optional.isPresent()) {
      recommendation = optional.get();
    } else {
      recommendation = new MemberRecommendation();
    }
    BeanUtils.copyProperties(request, recommendation);
    log.debug("recommended member: {}", recommendation);

    return memberRepository.findById(request.getMemberId()).map(m -> {
      recommendation.setMember(m);

      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
      try {
        recommendation.setStartedAt(df.parse(request.getStartedAt()));
        recommendation.setEndedAt(df.parse(request.getEndedAt()));
      } catch (ParseException e) {
        log.error("invalid date format", e);
      }

      memberRecommendationRepository.save(recommendation);

      RecommendedMemberInfo info = new RecommendedMemberInfo();
      BeanUtils.copyProperties(recommendation, info);
      return new ResponseEntity<>(info, HttpStatus.OK);
    }).orElseThrow(() -> new NotFoundException("member_not_found", "invalid member id"));
  }

  @PostMapping("/goods")
  public ResponseEntity<RecommendedGoodsInfo> createRecommendedGoods(
      @RequestBody CreateRecommendedGoodsRequest request) throws ParseException {
    log.debug("request: {}", request);

    GoodsRecommendation recommendation;
    Optional<GoodsRecommendation> optional
        = goodsRecommendationRepository.findByGoodsNo(request.getGoodsNo());
    if (optional.isPresent()) {
      recommendation = optional.get();
    } else {
      recommendation = new GoodsRecommendation();
    }
    BeanUtils.copyProperties(request, recommendation);
    log.debug("recommended goods: {}", recommendation);

    return goodsRepository.findByGoodsNo(request.getGoodsNo()).map(g -> {
      recommendation.setGoods(g);

      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
      try {
        recommendation.setStartedAt(df.parse(request.getStartedAt()));
        recommendation.setEndedAt(df.parse(request.getEndedAt()));
      } catch (ParseException e) {
        log.error("invalid date format", e);
      }

      goodsRecommendationRepository.save(recommendation);

      RecommendedGoodsInfo info = new RecommendedGoodsInfo();
      BeanUtils.copyProperties(recommendation, info);
      return new ResponseEntity<>(info, HttpStatus.OK);
    }).orElseThrow(() -> new NotFoundException("goods_not_found", "invalid goods no"));
  }

  @Data
  public static class CreateTrendRequest {
    private Long postId;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  public static class TrendInfo {
    private Long id;
    private Post post;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }

  @Data
  public static class CreateRecommendedMemberRequest {
    private Long memberId;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  public static class RecommendedMemberInfo {
    private Long id;
    private Member member;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }

  @Data
  public static class CreateRecommendedGoodsRequest {
    private String goodsNo;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  public static class RecommendedGoodsInfo {
    private String goodsNo;
    private Goods goods;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }
}
