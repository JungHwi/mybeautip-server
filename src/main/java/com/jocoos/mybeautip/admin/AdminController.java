package com.jocoos.mybeautip.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostContent;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;
import com.jocoos.mybeautip.recommendation.GoodsRecommendationRepository;
import com.jocoos.mybeautip.recommendation.KeywordRecommendation;
import com.jocoos.mybeautip.recommendation.KeywordRecommendationRepository;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import com.jocoos.mybeautip.recommendation.MemberRecommendationRepository;
import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import com.jocoos.mybeautip.recommendation.MotdRecommendationBase;
import com.jocoos.mybeautip.recommendation.MotdRecommendationBaseRepository;
import com.jocoos.mybeautip.recommendation.MotdRecommendationRepository;
import com.jocoos.mybeautip.recommendation.RecommendationController;
import com.jocoos.mybeautip.restapi.PostController;
import com.jocoos.mybeautip.schedules.Schedule;
import com.jocoos.mybeautip.schedules.ScheduleRepository;
import com.jocoos.mybeautip.store.StoreRepository;
import com.jocoos.mybeautip.tag.Tag;
import com.jocoos.mybeautip.tag.TagRepository;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
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
  private final MotdRecommendationBaseRepository motdRecommendationBaseRepository;
  private final KeywordRecommendationRepository keywordRecommendationRepository;
  private final ReportRepository reportRepository;
  private final StoreRepository storeRepository;
  private final VideoRepository videoRepository;
  private final VideoReportRepository videoReportRepository;
  private final ScheduleRepository scheduleRepository;
  private final TagRepository tagRepository;
  private final VideoService videoService;
  private final TagService tagService;
  private final GoodsService goodsService;
  private final MemberService memberService;

  public AdminController(PostRepository postRepository,
                         BannerRepository bannerRepository,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         MemberRecommendationRepository memberRecommendationRepository,
                         GoodsRecommendationRepository goodsRecommendationRepository,
                         MotdRecommendationRepository motdRecommendationRepository,
                         MotdRecommendationBaseRepository motdRecommendationBaseRepository,
                         KeywordRecommendationRepository keywordRecommendationRepository,
                         ReportRepository reportRepository,
                         StoreRepository storeRepository,
                         VideoRepository videoRepository,
                         VideoReportRepository videoReportRepository,
                         ScheduleRepository scheduleRepository, TagRepository tagRepository,
                         VideoService videoService,
                         TagService tagService,
                         GoodsService goodsService, MemberService memberService) {
    this.postRepository = postRepository;
    this.bannerRepository = bannerRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
    this.motdRecommendationBaseRepository = motdRecommendationBaseRepository;
    this.keywordRecommendationRepository = keywordRecommendationRepository;
    this.reportRepository = reportRepository;
    this.storeRepository = storeRepository;
    this.videoRepository = videoRepository;
    this.videoReportRepository = videoReportRepository;
    this.scheduleRepository = scheduleRepository;
    this.tagRepository = tagRepository;
    this.tagService = tagService;
    this.videoService = videoService;
    this.goodsService = goodsService;
    this.memberService = memberService;
  }

  @DeleteMapping("/posts/{id:.+}")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
    postRepository.findByIdAndDeletedAtIsNull(id).map(post -> {
      post.setDeletedAt(new Date());
      postRepository.save(post);
      tagService.decreaseRefCount(post.getDescription());
      tagService.removeHistory(post.getDescription(), TagService.TAG_POST, post.getId(), post.getCreatedBy());
      return Optional.empty();
    }).orElseThrow(() -> new NotFoundException("post_not_found", "post not found"));

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/posts")
  public ResponseEntity<PostController.PostInfo> createPost(@RequestBody CreatePostRequest request) {
    log.debug("request: {}", request);

    Post post = new Post();
    BeanUtils.copyProperties(request, post);
    post.setStartedAt(getRecommendedDate(request.getStartedAt()));
    post.setEndedAt(getRecommendedDate(request.getEndedAt()));
    postRepository.save(post);
    log.debug("saved post: {}", post);

    if (StringUtils.isNotEmpty(request.getDescription())) {
      tagService.increaseRefCount(request.getDescription());
      tagService.addHistory(request.getDescription(), TagService.TAG_POST, post.getId(), post.getCreatedBy());
    }

    Member me = memberService.currentMember();
    List<GoodsInfo> goodsInfoList = new ArrayList<>();
    List<String> goodsList = post.getGoods();
    for (String info : goodsList) {
      goodsService.generateGoodsInfo(info)
         .map(goodsInfoList::add)
         .orElseThrow(() -> new NotFoundException("goodsNo not found", "invalid good no"));
    }

    PostController.PostInfo info = new PostController.PostInfo(post, new MemberInfo(me), goodsInfoList);
    return new ResponseEntity<>(info, HttpStatus.OK);
  }

  @PostMapping("/banners")
  public ResponseEntity<BannerInfo> createTrend(@RequestBody CreateBannerRequest request) {
    log.debug("request: {}", request);

    Post post = postRepository.findByIdAndDeletedAtIsNull(request.getPostId())
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));

    Banner banner = new Banner();
    BeanUtils.copyProperties(request, banner);
    banner.setLink(String.format("/api/1/posts/%d", request.getPostId()));
    banner.setPost(post);
    banner.setCategory(1);
    banner.setStartedAt(getRecommendedDate(request.getStartedAt()));
    banner.setEndedAt(getRecommendedDate(request.getEndedAt()));

    banner = bannerRepository.save(banner);
    log.debug("banner: {}", banner);

    if (StringUtils.isNotEmpty(request.getDescription())) {
      tagService.touchRefCount(request.getDescription());
      tagService.addHistory(request.getDescription(), TagService.TAG_BANNER, banner.getId(), banner.getCreatedBy());
    }

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

         tagService.removeHistory(banner.getDescription(), TagService.TAG_BANNER, banner.getId(), banner.getCreatedBy());
         return Optional.empty();
       })
       .orElseThrow(() -> new NotFoundException("banner_not_found", "invalid banner id"));

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/memberDetails", params = {"isDeleted=true"})
  public ResponseEntity<Page<MemberDetailInfo>> getDeletedMemberDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));
    Page<Member> members = memberRepository.findByDeletedAtIsNotNull(pageable);
    Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping(value = "/memberDetails", params = {"isReported=true"})
  public ResponseEntity<Page<MemberDetailInfo>> getReportedMemberDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "reportCount"));
    Page<Member> members = memberRepository.findByVisibleAndReportCountNot(true, 0, pageable);

    Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping("/memberDetails")
  public ResponseEntity<Page<MemberDetailInfo>> getMemberDetails(
     @RequestParam(defaultValue = "true") boolean visible,
     @RequestParam(defaultValue = "0") int link,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "false") boolean isDeleted,
     @RequestParam(required = false) String sort) {

    Pageable pageable = null;
    if (sort != null) {
      Sort pagingSort = null;
      switch (sort) {
        case "video":
          pagingSort = new Sort(Sort.Direction.DESC, "publicVideoCount");
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
        case "order":
          pagingSort = new Sort(Sort.Direction.DESC, "orderCount");
          break;
        default:
          pagingSort = new Sort(Sort.Direction.DESC, "id");
      }

      pageable = PageRequest.of(page, size, pagingSort);
    } else {
      pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));
    }

    Page<Member> members = null;
    if (link > 0) {
      members = memberRepository.findByLinkAndVisible(link, visible, pageable);
    } else {
      members = memberRepository.findByVisible(visible, pageable);
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
      members = memberRepository.findByVisibleAndPushableAndUsernameContaining(true, pushable, username, pageable);
    } else {
      members = memberRepository.findByVisibleAndPushable(true, pushable, pageable);
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

    info.setSchedule(findScheduleByMember(m.getId()));
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

      info.setSchedule(findScheduleByMember(m.getMemberId()));
      return info;
    });

    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  private Schedule findScheduleByMember(Long memberId) {
    List<Schedule> schedules = scheduleRepository.findByCreatedByIdOrderByStartedAt(memberId);
    if (!CollectionUtils.isEmpty(schedules)) {
      return schedules.get(0);
    }

    return null;
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
     @RequestParam(required = false) List<String> goodses,
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

    if (!CollectionUtils.isEmpty(goodses)) {
      goods = goodsRepository.findByGoodsNoIn(goodses, pageable);
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

         Date baseDate = recommendation.getStartedAt();
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
                if (b.getMotdCount() > 0) {
                  motdRecommendationBaseRepository.updateMotdCount(b.getId(), -1);
                }
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
    return Dates.getRecommendedDate(date);
  }

  @GetMapping("/motdDetails")
  public ResponseEntity<Page<MotdDetailInfo>> getMotdDetails(
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "id") String sort,
     @RequestParam(defaultValue = "false") boolean isDeleted,
     @RequestParam(required = false) Long memberId) {

    Member me = memberService.currentMember();


    Pageable pageable;
    switch (sort) {
      case "views":
        pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "viewCount"));
        break;
      case "like":
        pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "likeCount"));
        break;
      case "comments":
        pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "commentCount"));
        break;
      case "watch":
        pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "totalWatchCount"));
        break;
      default:
        pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));
    }

    Page<Video> videos = null;
    if (memberId != null) {
      if (isDeleted) {
        videos = videoRepository.findByMemberIdAndTypeAndStateAndDeletedAtIsNotNull(memberId, "UPLOADED", "VOD", pageable);
      } else {
        videos = videoRepository.findByMemberIdAndTypeAndStateAndDeletedAtIsNull(memberId, "UPLOADED", "VOD", pageable);
      }
    } else {
      if (isDeleted) {
        videos = videoRepository.findByTypeAndStateInAndDeletedAtIsNotNull("UPLOADED", Lists.newArrayList("VOD"), pageable);
      } else {
        videos = videoRepository.findByTypeAndStateInAndDeletedAtIsNull("UPLOADED", Lists.newArrayList("VOD"), pageable);
      }
    }

    Page<MotdDetailInfo> details = videos.map(v -> {
      MotdDetailInfo info = new MotdDetailInfo(v);
      motdRecommendationRepository.findByVideoId(v.getId())
         .ifPresent(r -> info.setRecommendation(r));

      videoReportRepository.findByVideoIdAndCreatedById(v.getId(), me.getId())
         .ifPresent(r -> info.setVideoReportId(r.getId()));

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

  @GetMapping("/recommendedLiveDetails")
  public ResponseEntity<Page<RecommendationController.RecommendedMotdInfo>> getRecommendedLiveDetails(
     @RequestParam(defaultValue = "BROADCASTED") String type,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size,
     @RequestParam(defaultValue = "desc") String direction) {

    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.fromString(direction), "seq", "createdAt"));
    Page<MotdRecommendation> bases = motdRecommendationRepository.findByVideoTypeAndVideoDeletedAtIsNull(type, pageable);

    Page<RecommendationController.RecommendedMotdInfo> details = bases.map(m -> new RecommendationController.RecommendedMotdInfo(m, videoService.generateVideoInfo(m.getVideo())));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  private List<RecommendationController.RecommendedMotdInfo> createRecommendedMotd(MotdRecommendationBase base) {
    List<RecommendationController.RecommendedMotdInfo> motds = Lists.newArrayList();
    for (MotdRecommendation m : base.getMotds()) {
      motds.add(new RecommendationController.RecommendedMotdInfo(m, videoService.generateVideoInfo(m.getVideo())));
    }
    Collections.sort(motds, (RecommendationController.RecommendedMotdInfo m1, RecommendationController.RecommendedMotdInfo m2)
       -> m1.getSeq() < m2.getSeq() ? 1 : m1.getSeq() > m2.getSeq() ? -1 :
       m2.getCreatedAt().after(m1.getCreatedAt()) ? 1 : m1.getCreatedAt().after(m2.getCreatedAt()) ? -1 : 0);
    return motds;
  }

  @Transactional
  @PostMapping("/recommendedKeywords")
  public void CreateRecommendedKeywordsRequest(@Valid @RequestBody CreateRecommendedKeywordsRequest request) {

    log.debug("request: {}", request);

    List<RecommendedKeyword> items = request.getItems();
    KeywordRecommendation keyword;
    int seq = 1;

    for (RecommendedKeyword item : items) {
      int itemSeq = item.getSeq() != null ? item.getSeq() : seq++;

      switch (item.getCategory()) {
        case 1: // Member
          Member member = memberRepository.findByUsernameAndDeletedAtIsNullAndVisibleIsTrue(item.getWord())
             .orElseThrow(() -> new MemberNotFoundException(item.getWord()));

          keyword = keywordRecommendationRepository.findByMember(member).orElse(null);
          if (keyword == null) {
            keywordRecommendationRepository.save(new KeywordRecommendation(member, itemSeq));
          } else {
            keyword.setSeq(seq++);
            keywordRecommendationRepository.save(keyword);
          }
          break;
        case 2: // Tag
        default:
          Tag tag = tagRepository.findByName(item.getWord()).orElse(null);
          if (tag == null) {
            tag = tagRepository.save(new Tag(item.getWord(), 0));
          }
          keyword = keywordRecommendationRepository.findByTag(tag).orElse(null);
          if (keyword == null) {
            keywordRecommendationRepository.save(new KeywordRecommendation(tag, itemSeq));
          } else {
            keyword.setSeq(seq++);
            keywordRecommendationRepository.save(keyword);
          }
          break;
      }
    }
  }

  @Data
  public static class CreatePostRequest {
    @NotNull
    private int category;
    @NotNull
    @Size(max = 32)
    private String title;
    @NotNull
    @Size(max = 2000)
    private String description;
    @NotNull
    @Size(max = 255)
    private String thumbnailUrl;
    private int progress;
    private boolean opened;
    private Set<PostContent> contents;
    @NotNull
    private List<String> goods;
    @NotNull
    private String startedAt;
    @NotNull
    private String endedAt;
  }


  @Data
  public static class CreateBannerRequest {
    @NotNull
    @Size(max = 22)
    private String title;
    @NotNull
    @Size(max = 34)
    private String description;
    @NotNull
    @Size(max = 255)
    private String thumbnailUrl;
    @NotNull
    private int seq;
    @NotNull
    private Long postId;
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

  @Data
  private static class CreateRecommendedKeywordsRequest {
    @Valid
    @NotNull(message = "items must not be null")
    private List<RecommendedKeyword> items;
  }

  @Data
  private static class RecommendedKeyword {
    @NotNull(message = "category must not be null")
    private Integer category; // 1: Member, 2: Tag

    @NotNull(message = "word must not be null")
    private String word;

    private Integer seq;
  }

}
