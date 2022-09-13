package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.recommendation.*;
import com.jocoos.mybeautip.schedules.Schedule;
import com.jocoos.mybeautip.schedules.ScheduleRepository;
import com.jocoos.mybeautip.store.StoreRepository;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.tag.Tag;
import com.jocoos.mybeautip.tag.TagRepository;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

import static com.jocoos.mybeautip.global.exception.ErrorCode.*;
import static com.jocoos.mybeautip.domain.member.code.MemberStatus.ACTIVE;


@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminController {

//    private final PostRepository postRepository;
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
    private final OrderRepository orderRepository;
    private final TagRepository tagRepository;
    private final VideoService videoService;
    private final TagService tagService;
    private final GoodsService goodsService;
    private final LegacyMemberService legacyMemberService;

    public AdminController(
//            PostRepository postRepository,
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
                           ScheduleRepository scheduleRepository, OrderRepository orderRepository, TagRepository tagRepository,
                           VideoService videoService,
                           TagService tagService,
                           GoodsService goodsService, LegacyMemberService legacyMemberService) {
//        this.postRepository = postRepository;
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
        this.orderRepository = orderRepository;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
        this.videoService = videoService;
        this.goodsService = goodsService;
        this.legacyMemberService = legacyMemberService;
    }

    @GetMapping(value = "/memberDetails", params = {"isDeleted=true"})
    public ResponseEntity<Page<MemberDetailInfo>> getDeletedMemberDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Member> members = memberRepository.findByDeletedAtIsNotNull(pageable);
        Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping(value = "/memberDetails", params = {"isReported=true"})
    public ResponseEntity<Page<MemberDetailInfo>> getReportedMemberDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reportCount"));
        Page<Member> members = memberRepository.findByVisibleAndReportCountNot(true, 0, pageable);

        Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping(value = "/memberDetails", params = {"visible", "username"})
    public ResponseEntity<Page<MemberDetailInfo>> searchMemberDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean visible,
            @RequestParam String username) {

        log.debug("visible: {}, username: {}", visible, username);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Member> members = memberRepository.findByVisibleAndUsernameContaining(visible, username, pageable);

        Page<MemberDetailInfo> details = members.map(m -> memberToMemberDetails(m));
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping(value = "/memberDetails", params = {"visible", "email"})
    public ResponseEntity<Page<MemberDetailInfo>> searchMemberDetailsByEmail(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean visible,
            @RequestParam String email) {

        log.debug("visible: {}, email: {}", visible, email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Member> members = memberRepository.findByVisibleAndEmailContaining(visible, email, pageable);
        log.info("{}", members);

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
                    pagingSort = Sort.by(Sort.Direction.DESC, "publicVideoCount");
                    break;
                case "revenue":
                    pagingSort = Sort.by(Sort.Direction.DESC, "revenue");
                    break;
                case "point":
                    pagingSort = Sort.by(Sort.Direction.DESC, "point");
                    break;
                case "follower":
                    pagingSort = Sort.by(Sort.Direction.DESC, "followerCount");
                    break;
                case "following":
                    pagingSort = Sort.by(Sort.Direction.DESC, "followingCount");
                    break;
                case "report":
                    pagingSort = Sort.by(Sort.Direction.DESC, "reportCount");
                    break;
                case "order":
                    pagingSort = Sort.by(Sort.Direction.DESC, "orderCount");
                    break;
                default:
                    pagingSort = Sort.by(Sort.Direction.DESC, "id");
            }

            pageable = PageRequest.of(page, size, pagingSort);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        }

        Page<Member> members = null;
        if (link > 0) {
            members = memberRepository.findByLinkAndStatus(link, ACTIVE, pageable);
        } else {
            members = memberRepository.findByStatus(ACTIVE, pageable);
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

        List<Integer> links = Arrays.asList(1, 2, 4);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Member> members = null;
        if (!StringUtils.isBlank(username)) {
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

        int orderCount = orderRepository.countByCreatedByIdAndStateLessThanEqual(m.getId(), Order.State.CONFIRMED.getValue());
        info.setOrderCount(orderCount);

        info.setSchedule(findScheduleByMember(m.getId()));
        return info;
    }

    @GetMapping("/recommendedMemberDetails")
    public ResponseEntity<Page<MemberDetailInfo>> getRecommendedMemberDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "seq"));
        Page<MemberRecommendation> members = memberRecommendationRepository.findByMemberDeletedAtIsNull(pageable);

        Page<MemberDetailInfo> details = members.map(m -> {
            MemberDetailInfo info = new MemberDetailInfo(m.getMember(), m);

            int orderCount = orderRepository.countByCreatedByIdAndStateLessThanEqual(m.getMemberId(), Order.State.CONFIRMED.getValue());
            info.setOrderCount(orderCount);

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
                    throw new BadRequestException(ErrorCode.MEMBER_DUPLICATED, "Already member is recommended");
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
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword) {

        log.info("keyword: {}", keyword);

        Pageable pageable = null;
        if (sort != null) {
            Sort pagingSort = null;
            switch (sort) {
                case "order":
                    pagingSort = Sort.by(Sort.Direction.DESC, "orderCnt");
                    break;
                case "hit":
                    pagingSort = Sort.by(Sort.Direction.DESC, "hitCnt");
                    break;
                case "like":
                    pagingSort = Sort.by(Sort.Direction.DESC, "likeCount");
                    break;
                default:
                    pagingSort = Sort.by(Sort.Direction.DESC, "goodsNo");
            }

            pageable = PageRequest.of(page, size, pagingSort);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "goodsNo"));
        }

        Page<Goods> goods = null;
        if (!StringUtils.isBlank(code)) {
            goods = goodsRepository.findByStateAndCateCd(state, code, pageable);
        } else {
            goods = goodsRepository.findByState(state, pageable);
        }

        if (!CollectionUtils.isEmpty(goodses)) {
            goods = goodsRepository.findByGoodsNoIn(goodses, pageable);
        }

        if (!StringUtils.isBlank(keyword)) {
            goods = goodsRepository.findByGoodsNmContainingOrderByGoodsNoDesc(keyword, pageable);
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
                    throw new BadRequestException(ErrorCode.DUPLICATED_GOODS, "Already goods is recommended");
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
        }).orElseThrow(() -> new NotFoundException(GOODS_NOT_FOUND, "invalid goods no"));
    }

    @DeleteMapping("/recommendedGoods/{goodsNo:.+}")
    public ResponseEntity deleteRecommendedGoods(@PathVariable String goodsNo) {
        log.debug("deleted goodsNo: {}", goodsNo);

        return goodsRecommendationRepository.findByGoodsNo(goodsNo)
                .map(r -> {
                    goodsRecommendationRepository.delete(r);
                    return new ResponseEntity(HttpStatus.NO_CONTENT);

                }).orElseThrow(() -> new NotFoundException(GOODS_NOT_FOUND, "invalid goods no"));
    }

    @Transactional
    @PostMapping("/recommendedMotds")
    public ResponseEntity<RecommendationController.RecommendedMotdBaseInfo> createRecommendedMotd(
            @RequestBody CreateRecommendedMotdRequest request) {
        log.debug("request: {}", request);

        motdRecommendationRepository.findByVideoId(request.getVideoId())
                .ifPresent(r -> {
                    throw new BadRequestException(DUPLICATED_MOTDS, "Already motds is recommended");
                });
        MotdRecommendation recommendation = new MotdRecommendation();
        recommendation.setSeq(request.getSeq());

        return videoRepository.findByIdAndDeletedAtIsNull(request.getVideoId())
                .map(v -> {
                    recommendation.setVideo(v);
                    recommendation.setStartedAt(getRecommendedDate(request.getStartedAt()));
                    recommendation.setEndedAt(getRecommendedDate(request.getEndedAt()));

                    Date startedAt = recommendation.getStartedAt();

                    LocalDate localDate = DateUtils.toLocalDate(startedAt);
                    Date baseDate = DateUtils.toDate(localDate);
                    log.info("baseDate: {}", baseDate);

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
                }).orElseThrow(() -> new NotFoundException(VIDEO_NOT_FOUND, "invalid video id"));
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
                .orElseThrow(() -> new NotFoundException(VIDEO_NOT_FOUND, "invalid video id"));
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

    @GetMapping("/videoDetails")
    public ResponseEntity<Page<MotdDetailInfo>> getVideoDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam String type,
            @RequestParam List<String> states,
            @RequestParam(defaultValue = "false") boolean isDeleted) {

        log.debug("sort: {}, deleted: {}", sort, isDeleted);
        Pageable pageable = null;
        switch (sort) {
            case "views":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
                break;
            case "like":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
                break;
            case "comments":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "commentCount"));
                break;
            case "watch":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "totalWatchCount"));
                break;
            case "heart":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "heartCount"));
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        }

        Page<Video> videos = null;
        if (isDeleted) {
            videos = videoRepository.findByTypeAndStateInAndDeletedAtIsNotNull(type, states, pageable);
        } else {
            videos = videoRepository.findByTypeAndStateInAndDeletedAtIsNull(type, states, pageable);
        }

        return responseVideos(videos);
    }

    private ResponseEntity<Page<MotdDetailInfo>> responseVideos(Page<Video> videos) {

        Member admin = legacyMemberService.currentMember();
        Page<MotdDetailInfo> details = videos.map(v -> {
            MotdDetailInfo info = new MotdDetailInfo(v);
            motdRecommendationRepository.findByVideoId(v.getId())
                    .ifPresent(r -> info.setRecommendation(r));


            // FIXME: Video is reported by admin?
            videoReportRepository.findByVideoIdAndCreatedById(v.getId(), admin.getId())
                    .ifPresent(r -> info.setVideoReportId(r.getId()));

            Page<VideoReport> reports = videoReportRepository.findByVideoId(v.getId(), PageRequest.of(0, 1));
            info.setReportCount(reports.getTotalElements());

            return info;
        });

        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping(value = "/videoDetails", params = "memberId")
    public ResponseEntity<Page<MotdDetailInfo>> getVideoDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam String type,
            @RequestParam List<String> states,
            @RequestParam(defaultValue = "false") boolean isDeleted,
            @RequestParam Long memberId) {

        Pageable pageable = null;
        switch (sort) {
            case "views":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
                break;
            case "like":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
                break;
            case "comments":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "commentCount"));
                break;
            case "watch":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "totalWatchCount"));
                break;
            case "heart":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "heartCount"));
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        }

        Page<Video> videos = null;
        if (isDeleted) {
            videos = videoRepository.findByMemberIdAndTypeAndStateInAndDeletedAtIsNotNull(memberId, type, states, pageable);
        } else {
            videos = videoRepository.findByMemberIdAndTypeAndStateInAndDeletedAtIsNull(memberId, type, states, pageable);
        }

        return responseVideos(videos);
    }

    @GetMapping("/recommendedMotdDetails")
    public ResponseEntity<Page<RecommendationController.RecommendedMotdBaseInfo>> getRecommendedMotdDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), "baseDate"));
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), "seq", "createdAt"));
        Page<MotdRecommendation> bases = motdRecommendationRepository.findByVideoTypeAndVideoDeletedAtIsNull(type, pageable);

        Page<RecommendationController.RecommendedMotdInfo> details = bases.map(m -> new RecommendationController.RecommendedMotdInfo(m, videoService.generateVideoInfo(m.getVideo())));
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    private List<RecommendationController.RecommendedMotdInfo> createRecommendedMotd(MotdRecommendationBase base) {
        List<RecommendationController.RecommendedMotdInfo> motds = new ArrayList<>();
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
//        private Set<PostContent> contents;
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
