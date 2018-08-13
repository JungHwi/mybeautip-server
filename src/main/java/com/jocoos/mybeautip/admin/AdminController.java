package com.jocoos.mybeautip.admin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.recommendation.*;
import com.jocoos.mybeautip.restapi.VideoController;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminController {

  private final PostRepository postRepository;
  private final BannerRepository bannerRepository;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;
  private final MotdRecommendationRepository motdRecommendationRepository;
  private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");

  public AdminController(PostRepository postRepository,
                         BannerRepository bannerRepository,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         MemberRecommendationRepository memberRecommendationRepository,
                         GoodsRecommendationRepository goodsRecommendationRepository,
                         MotdRecommendationRepository motdRecommendationRepository) {
    this.postRepository = postRepository;
    this.bannerRepository = bannerRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
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

  @PostMapping("/banners")
  public ResponseEntity<BannerInfo> createTrend(@RequestBody CreateBannerRequest request) throws ParseException {
    log.debug("request: {}", request);

    Banner banner = new Banner();
    BeanUtils.copyProperties(request, banner);
    log.debug("banner: {}", banner);

    try {
      banner.setStartedAt(df.parse(request.getStartedAt()));
      banner.setEndedAt(df.parse(request.getEndedAt()));
    } catch (ParseException e) {
      log.error("invalid date format", e);
    }

    bannerRepository.save(banner);

    BannerInfo info = new BannerInfo();
    BeanUtils.copyProperties(banner, info);
    return new ResponseEntity<>(info, HttpStatus.OK);

  }

  @DeleteMapping("/banners/{id:.+}")
  public ResponseEntity<?> deleteBanner(@PathVariable Long id) {
    bannerRepository.findById(id)
       .map(banner -> {
         banner.setDeletedAt(new Date());
         bannerRepository.save(banner);
         return Optional.empty();
       })
       .orElseThrow(() -> new NotFoundException("banner_not_found", "invalid banner id"));

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/members")
  public ResponseEntity<RecommendedMemberInfo> createRecommendedMember(
      @RequestBody CreateRecommendedMemberRequest request) {
    log.debug("request: {}", request);

    Optional<MemberRecommendation> optional
        = memberRecommendationRepository.findById(request.getMemberId());
    MemberRecommendation recommendation = optional.orElseGet(MemberRecommendation::new);
    BeanUtils.copyProperties(request, recommendation);
    log.debug("recommended member: {}", recommendation);

    return memberRepository.findById(request.getMemberId()).map(m -> {
      recommendation.setMember(m);

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
      @RequestBody CreateRecommendedGoodsRequest request) {
    log.debug("request: {}", request);


    Optional<GoodsRecommendation> optional
        = goodsRecommendationRepository.findByGoodsNo(request.getGoodsNo());
    GoodsRecommendation recommendation = optional.orElseGet(GoodsRecommendation::new);
    BeanUtils.copyProperties(request, recommendation);
    log.debug("recommended goods: {}", recommendation);

    return goodsRepository.findByGoodsNo(request.getGoodsNo()).map(g -> {
      recommendation.setGoods(g);

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

  @PostMapping("/motd")
  public ResponseEntity<RecommendedMotdInfo> createRecommendedMotd(
    @RequestBody CreateRecommendedMotdRequest request) {
    log.debug("request: {}", request);

    Optional<MotdRecommendation> optional = motdRecommendationRepository.findByVideoId(request.getVideoId());
    MotdRecommendation recommendation = optional.orElseGet(MotdRecommendation::new);
    BeanUtils.copyProperties(request, recommendation);
    log.debug("recommended motd: {}", recommendation);

    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
    try {
      recommendation.setStartedAt(df.parse(request.getStartedAt()));
      recommendation.setEndedAt(df.parse(request.getEndedAt()));
    } catch (ParseException e) {
      log.error("invalid date format", e);
    }

    motdRecommendationRepository.save(recommendation);

    RecommendedMotdInfo info = new RecommendedMotdInfo();
    BeanUtils.copyProperties(recommendation, info);
    return new ResponseEntity<>(info, HttpStatus.OK);
  }

  @Data
  public static class CreateBannerRequest {
    @NotNull @Size(max = 22)
    private String title;
    @NotNull @Size(max = 34)
    private String description;
    @NotNull @Size(max = 255)
    private String thumbnailUrl;
    @NotNull
    private int seq;
    @NotNull
    private int category;
    @NotNull @Size(max = 255)
    private String link;
    @NotNull
    private String startedAt;
    @NotNull
    private String endedAt;
  }

  @Data
  public static class BannerInfo {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private int seq;
    private int category;
    private String link;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }

  @Data
  private static class CreateRecommendedMemberRequest {
    private Long memberId;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  private static class RecommendedMemberInfo {
    private Long id;
    private Member member;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }

  @Data
  private static class CreateRecommendedGoodsRequest {
    private String goodsNo;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  private static class RecommendedGoodsInfo {
    private String goodsNo;
    private Goods goods;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }

  @Data
  private static class CreateRecommendedMotdRequest {
    private Long videoId;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  private static class RecommendedMotdInfo {
    private Long id;
    private VideoController.VideoInfo video;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }
}