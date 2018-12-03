package com.jocoos.mybeautip.admin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.recommendation.*;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.store.StoreRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;

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
  private final ReportRepository reportRepository;
  private final StoreRepository storeRepository;
  private final VideoRepository videoRepository;
  private final VideoReportRepository videoReportRepository;
  private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");

  public AdminController(PostRepository postRepository,
                         BannerRepository bannerRepository,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         MemberRecommendationRepository memberRecommendationRepository,
                         GoodsRecommendationRepository goodsRecommendationRepository,
                         MotdRecommendationRepository motdRecommendationRepository,
                         ReportRepository reportRepository,
                         StoreRepository storeRepository,
                         VideoRepository videoRepository,
                         VideoReportRepository videoReportRepository) {
    this.postRepository = postRepository;
    this.bannerRepository = bannerRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
    this.reportRepository = reportRepository;
    this.storeRepository = storeRepository;
    this.videoRepository = videoRepository;
    this.videoReportRepository = videoReportRepository;
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
  public ResponseEntity<BannerInfo> createTrend(@RequestBody CreateBannerRequest request) {
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

  @GetMapping("/memberDetails")
  public ResponseEntity<Page<MemberDetailInfo>> getMemberDetails(
     @RequestParam List<Integer> links,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "false") boolean isDeleted,
     @RequestParam(required = false) String sort) {

    Pageable pageable = null;
    if (sort != null) {
      Sort pagingSort = null;
      switch (sort) {
        case "video":
          pagingSort = new Sort(Sort.Direction.DESC, "videoCount");
          break;
        case "revenue":
          pagingSort = new Sort(Sort.Direction.DESC, "revenue");
          break;
        case "point":
          pagingSort = new Sort(Sort.Direction.DESC, "point");
          break;
        case "follower":
          pagingSort = new Sort(Sort.Direction.DESC, "followerCount");
          break;
        case "following":
          pagingSort = new Sort(Sort.Direction.DESC, "followingCount");
          break;
        case "report":
          pagingSort = new Sort(Sort.Direction.DESC, "reportCount");
          break;
        default:
          pagingSort = new Sort(Sort.Direction.DESC, "id");
      }

      pageable = PageRequest.of(page, size, pagingSort);
    } else {
      pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));
    }

    Page<Member> members = null;
    if(isDeleted) {
      members = memberRepository.findByLinkInAndEmailIsNotNullAndDeletedAtIsNotNull(links, pageable);
    } else {
      members = memberRepository.findByLinkInAndDeletedAtIsNull(links, pageable);
    }

    Page<MemberDetailInfo> details = members.map(m -> {
      MemberDetailInfo info = new MemberDetailInfo(m);
      Optional<MemberRecommendation> recommendation = memberRecommendationRepository.findByMemberId(m.getId());
      recommendation.ifPresent(r -> info.setRecommendation(r));

      Page<Report> reports = reportRepository.findByYouId(m.getId(), PageRequest.of(1, 1));
      if (reports != null) {
        info.setReportCount(reports.getTotalElements());
      }
      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping("/recommendedMemberDetails")
  public ResponseEntity<Page<MemberDetailInfo>> getRecommendedMemberDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.ASC, "seq"));
    Page<MemberRecommendation> members = memberRecommendationRepository.findByMemberDeletedAtIsNull(pageable);

    Page<MemberDetailInfo> details = members.map(m -> {
      MemberDetailInfo info = new MemberDetailInfo(m.getMember(), m);
      Page<Report> reports = reportRepository.findByYouId(m.getMemberId(), PageRequest.of(1, 1));
      if (reports != null) {
        info.setReportCount(reports.getTotalElements());
      }

      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @PostMapping("/recommendedMembers")
  public ResponseEntity<RecommendedMemberInfo> createRecommendedMember(
      @RequestBody CreateRecommendedMemberRequest request) {

    log.debug("request: {}", request);

    memberRecommendationRepository.findByMemberId(request.getMemberId())
       .ifPresent(r -> {
         throw new BadRequestException("member_duplicated", "Already member is recommended");
       });

    MemberRecommendation recommendation = new MemberRecommendation();
    recommendation.setSeq(request.getSeq());

    return memberRepository.findByIdAndDeletedAtIsNull(request.getMemberId()).map(m -> {
      recommendation.setMember(m);

      try {
        recommendation.setStartedAt(df.parse(request.getStartedAt()));
        recommendation.setEndedAt(df.parse(request.getEndedAt()));
      } catch (ParseException e) {
        log.error("invalid date format", e);
      }

      log.debug("recommended member: {}", recommendation);
      memberRecommendationRepository.save(recommendation);

      RecommendedMemberInfo info = new RecommendedMemberInfo();
      BeanUtils.copyProperties(recommendation, info);
      return new ResponseEntity<>(info, HttpStatus.OK);
    }).orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));
  }

  @GetMapping("/goodsDetails")
  public ResponseEntity<Page<GoodsDetailInfo>> getGoodsDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "false") boolean isDeleted,
     @RequestParam(defaultValue = "1") int state,
     @RequestParam(required = false) String code,
     @RequestParam(required = false) String sort) {

    Pageable pageable = null;
    if (sort != null) {
      Sort pagingSort = null;
      switch (sort) {
        case "order":
          pagingSort = new Sort(Sort.Direction.DESC, "orderCnt");
          break;
        case "hit":
          pagingSort = new Sort(Sort.Direction.DESC, "hitCnt");
          break;
        case "like":
          pagingSort = new Sort(Sort.Direction.DESC, "likeCount");
          break;
        default:
          pagingSort = new Sort(Sort.Direction.DESC, "goodsNo");
      }

      pageable = PageRequest.of(page, size, pagingSort);
    } else {
      pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "goodsNo"));
    }

    Page<Goods> goods = null;
    if (!Strings.isNullOrEmpty(code)) {
      goods = goodsRepository.findByStateAndCateCd(state, code, pageable);
    } else {
      goods = goodsRepository.findByState(state, pageable);
    }

    Page<GoodsDetailInfo> details = goods.map(g -> {
      GoodsDetailInfo info = new GoodsDetailInfo(g);
      goodsRecommendationRepository.findByGoodsNo(g.getGoodsNo()).ifPresent(r -> info.setRecommendation(r));
      storeRepository.findById(g.getScmNo()).ifPresent(s -> info.setStore(s));
      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @PostMapping("/recommendedGoods")
  public ResponseEntity<RecommendedGoodsInfo> createRecommendedGoods(
      @RequestBody CreateRecommendedGoodsRequest request) {
    log.debug("request: {}", request);

    goodsRecommendationRepository.findByGoodsNo(request.getGoodsNo())
       .ifPresent(r -> {
         throw new BadRequestException("duplicated_goods", "Already goods is recommended");
       });

    GoodsRecommendation recommendation = new GoodsRecommendation();
    recommendation.setSeq(request.getSeq());


    return goodsRepository.findByGoodsNo(request.getGoodsNo()).map(g -> {
      recommendation.setGoods(g);
      recommendation.setGoodsNo(g.getGoodsNo());

      try {
        recommendation.setStartedAt(df.parse(request.getStartedAt()));
        recommendation.setEndedAt(df.parse(request.getEndedAt()));
      } catch (ParseException e) {
        log.error("invalid date format", e);
      }

      log.debug("recommended goods: {}", recommendation);
      goodsRecommendationRepository.save(recommendation);

      RecommendedGoodsInfo info = new RecommendedGoodsInfo();
      BeanUtils.copyProperties(recommendation, info);
      return new ResponseEntity<>(info, HttpStatus.OK);
    }).orElseThrow(() -> new NotFoundException("goods_not_found", "invalid goods no"));
  }

  @DeleteMapping("/recommendedGoods/{goodsNo:.+}")
  public ResponseEntity deleteRecommendedGoods(@PathVariable String goodsNo) {
    log.debug("deleted goodsNo: {}", goodsNo);

    return goodsRecommendationRepository.findByGoodsNo(goodsNo)
       .map(r -> {
         goodsRecommendationRepository.delete(r);
         return new ResponseEntity(HttpStatus.NO_CONTENT);

       }).orElseThrow(() -> new NotFoundException("goods_not_found", "invalid goods no"));
  }

  @PostMapping("/recommendedMotds")
  public ResponseEntity<RecommendedMotdInfo> createRecommendedMotd(
    @RequestBody CreateRecommendedMotdRequest request) {
    log.debug("request: {}", request);

    motdRecommendationRepository.findByVideoId(request.getVideoId())
      .ifPresent(r -> {
        throw new BadRequestException("duplicated_motds", "Already motds is recommended");
      });
    MotdRecommendation recommendation = new MotdRecommendation();
    recommendation.setSeq(request.getSeq());

    return videoRepository.findByIdAndDeletedAtIsNull(request.getVideoId())
       .map(v -> {
         recommendation.setVideo(v);
         SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
         try {
           recommendation.setStartedAt(df.parse(request.getStartedAt()));
           recommendation.setEndedAt(df.parse(request.getEndedAt()));
         } catch (ParseException e) {
           log.error("invalid date format", e);
         }

         log.debug("recommended motd: {}", recommendation);
         motdRecommendationRepository.save(recommendation);

         RecommendedMotdInfo info = new RecommendedMotdInfo();
         BeanUtils.copyProperties(recommendation, info);
         return new ResponseEntity<>(info, HttpStatus.OK);
       }).orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id"));
  }

  @GetMapping("/motdDetails")
  public ResponseEntity<Page<MotdDetailInfo>> getMotdDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));;
    Page<Video> videos = videoRepository.findByTypeAndState("UPLOADED", "VOD", pageable);

    Page<MotdDetailInfo> details = videos.map(v -> {
      MotdDetailInfo info = new MotdDetailInfo(v);
      motdRecommendationRepository.findByVideoId(v.getId())
         .ifPresent(r -> info.setRecommendation(r));

      Page<VideoReport> reports = videoReportRepository.findByVideoId(v.getId(), PageRequest.of(0, 1));
      info.setReportCount(reports.getTotalElements());

      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping("/recommendedMotdDetails")
  public ResponseEntity<Page<MotdDetailInfo>> getRecommendedMotdDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.ASC, "seq"));;
    Page<MotdRecommendation> videos = motdRecommendationRepository.findByVideoDeletedAtIsNull(pageable);

    Page<MotdDetailInfo> details = videos.map(v -> {
      MotdDetailInfo info = new MotdDetailInfo(v.getVideo());
      info.setRecommendation(v);

      Page<VideoReport> reports = videoReportRepository.findByVideoId(v.getVideo().getId(), PageRequest.of(0, 1));
      info.setReportCount(reports.getTotalElements());
      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
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
    private int seq;
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
    private Integer state;  // 상태 (1: 구매가능, 2:품절, 3: 구매불가(판매 안함), 4: 노출안함, 5: 삭제됨)
    private Goods goods;
    private int seq;
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