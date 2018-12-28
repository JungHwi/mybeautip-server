package com.jocoos.mybeautip.admin;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.tag.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import com.jocoos.mybeautip.store.StoreRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminController {
  private static final SimpleDateFormat RECOMMENDED_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
  private static final SimpleDateFormat BASE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

  private final PostRepository postRepository;
  private final BannerRepository bannerRepository;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;
  private final MotdRecommendationRepository motdRecommendationRepository;
  private final MotdRecommendationBaseRepository motdRecommendationBaseRepository;
  private final ReportRepository reportRepository;
  private final StoreRepository storeRepository;
  private final VideoRepository videoRepository;
  private final VideoReportRepository videoReportRepository;
  private final VideoService videoService;
  private final TagService tagService;
  private final MemberService memberService;

  public AdminController(PostRepository postRepository,
                         BannerRepository bannerRepository,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         MemberRecommendationRepository memberRecommendationRepository,
                         GoodsRecommendationRepository goodsRecommendationRepository,
                         MotdRecommendationRepository motdRecommendationRepository,
                         MotdRecommendationBaseRepository motdRecommendationBaseRepository,
                         ReportRepository reportRepository,
                         StoreRepository storeRepository,
                         VideoRepository videoRepository,
                         VideoReportRepository videoReportRepository,
                         VideoService videoService,
                         TagService tagService,
                         MemberService memberService) {
    this.postRepository = postRepository;
    this.bannerRepository = bannerRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
    this.motdRecommendationBaseRepository = motdRecommendationBaseRepository;
    this.reportRepository = reportRepository;
    this.storeRepository = storeRepository;
    this.videoRepository = videoRepository;
    this.videoReportRepository = videoReportRepository;
    this.tagService = tagService;
    this.videoService = videoService;
    this.memberService = memberService;
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
  
    if (StringUtils.isNotEmpty(request.getDescription())) {
      List<String> tags = tagService.getHashTagsAndIncreaseRefCount(request.getDescription());
      if (tags != null && tags.size() > 0) {
        // Log TagHistory
        tagService.logHistory(tags, TagService.TagCategory.POST, memberService.currentMember());
      }
    }

    banner.setStartedAt(getRecommendedDate(request.getStartedAt()));
    banner.setEndedAt(getRecommendedDate(request.getEndedAt()));

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
  
         tagService.decreaseRefCount(banner.getDescription());
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
    if (isDeleted) {
      members = memberRepository.findByLinkInAndEmailIsNotNullAndDeletedAtIsNotNull(links, pageable);
    } else {
      members = memberRepository.findByLinkInAndDeletedAtIsNull(links, pageable);
    }

    Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping(value = "/memberDetails", params = {"pushable"})
  public ResponseEntity<Page<MemberDetailInfo>> getSearchMemberDetails(
     @RequestParam(defaultValue = "false") boolean pushable,
     @RequestParam(required = false) String username,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "100") int size) {

    List<Integer> links = Lists.newArrayList(1, 2, 4);
    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));
    Page<Member> members = null;
    if (!Strings.isNullOrEmpty(username)) {
      members = memberRepository.findByLinkInAndPushableAndDeletedAtIsNullAndUsernameContaining(links, pushable, username, pageable);
    } else {
      members = memberRepository.findByLinkInAndPushableAndDeletedAtIsNull(links, pushable, pageable);
    }

    Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  private MemberDetailInfo memberToMemberDetails(Member m) {
    MemberDetailInfo info = new MemberDetailInfo(m);
    Optional<MemberRecommendation> recommendation = memberRecommendationRepository.findByMemberId(m.getId());
    recommendation.ifPresent(r -> info.setRecommendation(r));

    Page<Report> reports = reportRepository.findByYouId(m.getId(), PageRequest.of(1, 1));
    if (reports != null) {
      info.setReportCount(reports.getTotalElements());
    }
    return info;
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
      recommendation.setStartedAt(getRecommendedDate(request.getStartedAt()));
      recommendation.setEndedAt(getRecommendedDate(request.getEndedAt()));
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
      recommendation.setStartedAt(getRecommendedDate(request.getStartedAt()));
      recommendation.setEndedAt(getRecommendedDate(request.getEndedAt()));

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

  @Transactional
  @PostMapping("/recommendedMotds")
  public ResponseEntity<RecommendationController.RecommendedMotdBaseInfo> createRecommendedMotd(
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
         recommendation.setStartedAt(getRecommendedDate(request.getStartedAt()));
         recommendation.setEndedAt(getRecommendedDate(request.getEndedAt()));

         Date baseDate = getBaseDate(request.getStartedAt());
         log.debug("baseDate: {}", baseDate);

         Optional<MotdRecommendationBase> base = motdRecommendationBaseRepository.findByBaseDate(baseDate);
         MotdRecommendationBase newBase = null;
         if (base.isPresent()) {
           newBase = base.get();
           motdRecommendationBaseRepository.updateMotdCount(base.get().getId(), 1);

         } else {
           newBase = new MotdRecommendationBase();
           newBase.setBaseDate(baseDate);
           newBase.setMotdCount(1);
           motdRecommendationBaseRepository.save(newBase);
         }

         recommendation.setBaseId(newBase.getId());
         motdRecommendationRepository.save(recommendation);

         RecommendationController.RecommendedMotdBaseInfo info =
            new RecommendationController.RecommendedMotdBaseInfo(newBase);
         BeanUtils.copyProperties(recommendation, info);
         return new ResponseEntity<>(info, HttpStatus.OK);
       }).orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id"));
  }


  @Transactional
  @DeleteMapping("/recommendedMotds/{videoId:.+}")
  public ResponseEntity deleteRecommendedMotds(@PathVariable Long videoId) {
    log.debug("deleted motd video id: {}", videoId);

    return motdRecommendationRepository.findByVideoId(videoId)
       .map(r -> {
         motdRecommendationRepository.delete(r);
         motdRecommendationBaseRepository.findById(r.getBaseId())
          .ifPresent(b -> {
            if (b.getMotdCount() == 1) {
              motdRecommendationBaseRepository.delete(b);
            } else {
              motdRecommendationBaseRepository.updateMotdCount(b.getId(), -1);
            }
          });
         return new ResponseEntity(HttpStatus.NO_CONTENT);
       })
       .orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id"));
  }

  private List<RecommendationController.RecommendedMotdInfo> createMotdList(Iterable<MotdRecommendation> recommendations) {
    List<RecommendationController.RecommendedMotdInfo> info = new ArrayList<>();
    for (MotdRecommendation recommendation : recommendations) {
      info.add(new RecommendationController.RecommendedMotdInfo(recommendation, videoService.generateVideoInfo(recommendation.getVideo())));
    }
    return info;
  }

  private Date getRecommendedDate(String date) {
    try {
      return RECOMMENDED_DATE_FORMAT.parse(date);
    } catch (ParseException e) {
      log.error("invalid recommended date format", e);
      throw new BadRequestException("invalid date format", e.getMessage() + " - " + date);
    }
  }

  private Date getBaseDate(String date) {
    try {
      return BASE_DATE_FORMAT.parse(date);
    } catch (ParseException e) {
      log.error("invalid base date format", e);
      throw new BadRequestException("invalid date format", e.getMessage() + " - " + date);
    }
  }


  @GetMapping("/motdDetails")
  public ResponseEntity<Page<MotdDetailInfo>> getMotdDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "false") boolean isDeleted,
     @RequestParam(required = false) Long memberId) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));;
    Page<Video> videos = null;
    if (memberId != null) {
      if (isDeleted) {
        videos = videoRepository.findByMemberIdAndTypeAndStateAndDeletedAtIsNotNull(memberId, "UPLOADED", "VOD", pageable);
      } else {
        videos = videoRepository.findByMemberIdAndTypeAndStateAndDeletedAtIsNull(memberId, "UPLOADED", "VOD", pageable);
      }
    } else {
      if (isDeleted) {
        videos = videoRepository.findByTypeAndStateAndDeletedAtIsNotNull("UPLOADED", "VOD", pageable);
      } else {
        videos = videoRepository.findByTypeAndStateAndDeletedAtIsNull("UPLOADED", "VOD", pageable);
      }
    }

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
  public ResponseEntity<Page<RecommendationController.RecommendedMotdBaseInfo>> getRecommendedMotdDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "desc") String direction) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.fromString(direction), "baseDate"));
    Page<MotdRecommendationBase> bases = motdRecommendationBaseRepository.findAll(pageable);

    Page<RecommendationController.RecommendedMotdBaseInfo> details = bases.map(b -> {
      RecommendationController.RecommendedMotdBaseInfo info = new RecommendationController.RecommendedMotdBaseInfo(b, createRecommendedMotd(b));
      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  private List<RecommendationController.RecommendedMotdInfo> createRecommendedMotd(MotdRecommendationBase base) {
    List<RecommendationController.RecommendedMotdInfo> motds = Lists.newArrayList();
    for (MotdRecommendation m : base.getMotds()) {
      motds.add(new RecommendationController.RecommendedMotdInfo(m, videoService.generateVideoInfo(m.getVideo())));
    }
    return motds;
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
    private Integer state;  // 상태 (0: 구매가능, 1:품절, 2: 구매불가(판매 안함), 3: 노출안함, 4: 삭제됨)
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
}