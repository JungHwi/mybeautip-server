package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.domain.member.dto.MemberDetailRequest;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.member.comment.*;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.revenue.*;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.search.KeywordService;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;
import com.jocoos.mybeautip.video.*;
import com.jocoos.mybeautip.video.scrap.LegacyVideoScrapService;
import com.jocoos.mybeautip.video.scrap.VideoScrap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;


@Slf4j
@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class LegacyMemberController {

    private static final String MEMBER_NOT_FOUND = "member.not_found";
    private final LegacyMemberService legacyMemberService;
    private final GoodsService goodsService;
    private final LegacyVideoService legacyVideoService;
    private final PostProcessService postProcessService;
    private final MessageService messageService;
    private final MentionService mentionService;
    private final KeywordService keywordService;
    private final LegacyNotificationService legacyNotificationService;
    private final MemberRepository memberRepository;
    private final FollowingRepository followingRepository;
    //    private final PostLikeRepository postLikeRepository;
    private final GoodsLikeRepository goodsLikeRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final RevenueRepository revenueRepository;
    private final RevenuePaymentRepository revenuePaymentRepository;
    private final LegacyVideoScrapService legacyVideoScrapService;
    private final CommentService commentService;
    private final MemberTermService memberTermService;


    @Value("${mybeautip.store.image-path.domain}")
    private String storeImagePrefix;
    @Value("${mybeautip.store.image-path.cover-suffix}")
    private String storeImageSuffix;
    @Value("${mybeautip.store.image-path.thumbnail-suffix}")
    private String storeImageThumbnailSuffix;
    @Deprecated
    @Value("${mybeautip.point.earn-ratio}")
    private int pointRatio;
    @Deprecated
    @Value("${mybeautip.revenue.revenue-ratio-live}")
    private int revenueRatio;

    public LegacyMemberController(LegacyMemberService legacyMemberService,
                                  GoodsService goodsService,
                                  LegacyVideoService legacyVideoService,
                                  MemberRepository memberRepository,
                                  FollowingRepository followingRepository,
//                                  PostLikeRepository postLikeRepository,
                                  GoodsLikeRepository goodsLikeRepository,
                                  StoreLikeRepository storeLikeRepository,
                                  VideoLikeRepository videoLikeRepository,
                                  CommentRepository commentRepository,
                                  CommentLikeRepository commentLikeRepository,
                                  RevenueRepository revenueRepository,
                                  PostProcessService postProcessService,
                                  MessageService messageService,
                                  MentionService mentionService,
                                  KeywordService keywordService,
                                  LegacyNotificationService legacyNotificationService,
                                  RevenuePaymentRepository revenuePaymentRepository,
                                  LegacyVideoScrapService legacyVideoScrapService,
                                  CommentService commentService,
                                  MemberTermService memberTermService) {
        this.legacyMemberService = legacyMemberService;
        this.goodsService = goodsService;
        this.legacyVideoService = legacyVideoService;
        this.memberRepository = memberRepository;
        this.followingRepository = followingRepository;
//        this.postLikeRepository = postLikeRepository;
        this.goodsLikeRepository = goodsLikeRepository;
        this.storeLikeRepository = storeLikeRepository;
        this.videoLikeRepository = videoLikeRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.revenueRepository = revenueRepository;
        this.postProcessService = postProcessService;
        this.messageService = messageService;
        this.mentionService = mentionService;
        this.keywordService = keywordService;
        this.legacyNotificationService = legacyNotificationService;
        this.revenuePaymentRepository = revenuePaymentRepository;
        this.legacyVideoScrapService = legacyVideoScrapService;
        this.commentService = commentService;
        this.memberTermService = memberTermService;
    }

    @GetMapping("/me")
    public EntityModel<MemberMeInfo> getMe(Principal principal,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        log.debug("member id: {}", principal.getName());

        List<TermTypeResponse> termTypeResponses =
                memberTermService.getOptionalTermAcceptStatus(Long.parseLong(principal.getName()));

        return memberRepository.findByIdAndDeletedAtIsNull(Long.parseLong(principal.getName()))
                .map(m -> EntityModel.of(new MemberMeInfo(m, pointRatio, revenueRatio, termTypeResponses)))
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    }

    @PatchMapping()
    public ResponseEntity<MemberInfo> updateMember(@RequestBody UpdateMemberRequest updateMemberRequest,
                                                   @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        log.debug("member id: {}", legacyMemberService.currentMemberId());

        Member member = memberRepository.findByIdAndDeletedAtIsNull(legacyMemberService.currentMemberId())
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        if (!member.isVisible()) { // when first called
            if (StringUtils.isEmpty(updateMemberRequest.getUsername())) {
                throw new BadRequestException("Username required.");
            }
        }

        legacyMemberService.checkUsernameValidation(member.getId(), updateMemberRequest.getUsername(), lang);

        member = legacyMemberService.updateMember(updateMemberRequest, member);
        return new ResponseEntity<>(legacyMemberService.getMemberInfo(member), HttpStatus.OK);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestBody MultipartFile avatar) {
        String avatarUrl = legacyMemberService.uploadAvatar(avatar);
        Map<String, String> result = new HashMap<>();
        result.put("avatar_url", avatarUrl);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PutMapping(value = "/detail")
    public ResponseEntity updateDetailInfo(@RequestBody MemberDetailRequest request) {
        long memberId = legacyMemberService.currentMemberId();
        request.setMemberId(memberId);

        legacyMemberService.updateDetailInfo(request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/detail")
    public ResponseEntity<MemberDetailResponse> getDetailInfo() {
        long memberId = legacyMemberService.currentMemberId();

        MemberDetailResponse result = legacyMemberService.getDetailInfo(memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public CursorResponse getMembers(@RequestParam(defaultValue = "20") int count,
                                     @RequestParam(required = false) String cursor,
                                     @RequestParam(required = false) String keyword,
                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        List<MemberInfo> members = new ArrayList<>();
        String nextCursor = null;

        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.trim();
        }

        if (keyword == null || (!StringUtils.containsWhitespace(keyword) && StringUtils.isNotEmpty(keyword))) {
            Slice<Member> list = findMembers(keyword, cursor, count);
            list.stream().forEach(m -> members.add(legacyMemberService.getMemberInfo(m)));

            if (members.size() > 0) {
                nextCursor = String.valueOf(members.get(members.size() - 1).getCreatedAt().getTime());
            }
        }

        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.trim();
            try {
                keywordService.updateKeywordCount(keyword);
                keywordService.logHistory(keyword, KeywordService.KeywordCategory.MEMBER, legacyMemberService.currentMember());
            } catch (ConcurrencyFailureException e) { // Ignore
                log.warn("getMembers throws ConcurrencyFailureException: " + keyword);
            }
        }

        return new CursorResponse.Builder<>("/api/1/members", members)
                .withKeyword(keyword)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    // TODO: Refactor find member to MemberService
    private Slice<Member> findMembers(String keyword, String cursor, int count) {
        Cursor<Date> dateCursor;

        if (StringUtils.isBlank(cursor)) {
            dateCursor = new Cursor<>(keyword, null, count);
        } else {
            Date toDate;
            try {
                toDate = new Date(Long.parseLong(cursor));
                dateCursor = new Cursor<>(keyword, toDate, count);
            } catch (NumberFormatException e) {
                log.error("Cannot convert cursor to Date", e);
                throw new BadRequestException("invalid cursor type");
            }
        }

        return findMembers(dateCursor);
    }

    private Slice<Member> findMembers(Cursor<Date> cursor) {
        Slice<Member> list;
        log.debug("Cursor: {}", cursor);

        PageRequest page = PageRequest.of(0, cursor.getCount(), Sort.by("createdAt").descending());

        if (cursor.hasCursor() && cursor.hasKeyword()) {
            list = memberRepository.findByCreatedAtBeforeAndDeletedAtIsNullAndVisibleIsTrueAndUsernameContainingOrIntroContaining(cursor.getCursor(), cursor.getKeyword(), cursor.getKeyword(), page);
        } else if (cursor.hasCursor()) {
            list = memberRepository.findByCreatedAtBeforeAndDeletedAtIsNull(cursor.getCursor(), page);
        } else if (cursor.hasKeyword()) {
            list = memberRepository.findByDeletedAtIsNullAndVisibleIsTrueAndUsernameContainingOrIntroContaining(cursor.getKeyword(), cursor.getKeyword(), page);
        } else {
            list = memberRepository.findByDeletedAtIsNullAndVisibleIsTrue(page);
        }

        return list;
    }

    @GetMapping("/{id:.+}")
    public MemberInfo getMember(@PathVariable Long id,
                                @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        return memberRepository.findByIdAndDeletedAtIsNull(id).map(legacyMemberService::getMemberInfo)
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    }

    @GetMapping(value = "/me/like_count")
    public LikeCountResponse getMyLikesCount() {
        LikeCountResponse response = new LikeCountResponse();
        response.setGoods(goodsLikeRepository.countByCreatedById(legacyMemberService.currentMemberId()));
        response.setStore(storeLikeRepository.countByCreatedById(legacyMemberService.currentMemberId()));
//        response.setPost(postLikeRepository.countByCreatedByIdAndPostDeletedAtIsNull(legacyMemberService.currentMemberId()));
        response.setVideo(videoLikeRepository.countByCreatedByIdAndVideoDeletedAtIsNullAndStatus(legacyMemberService.currentMemberId(), LIKE));
        return response;
    }

    @GetMapping(value = "/{id:.+}/like_count")
    public LikeCountResponse getMemberLikesCount(@PathVariable Long id,
                                                 @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
        LikeCountResponse response = new LikeCountResponse();
        response.setGoods(goodsLikeRepository.countByCreatedById(id));
        response.setStore(storeLikeRepository.countByCreatedById(id));
//        response.setPost(postLikeRepository.countByCreatedByIdAndPostDeletedAtIsNull(id));
        response.setVideo(videoLikeRepository.countByCreatedByIdAndVideoDeletedAtIsNullAndStatus(id, LIKE));
        return response;
    }

    @GetMapping(value = "/me/likes")
    public CursorResponse getMyLikes(@RequestParam String category,
                                     @RequestParam(defaultValue = "20") int count,
                                     @RequestParam(required = false) Long cursor,
                                     @RequestParam(name = "broker", required = false) Long broker) {
        return createLikeResponse(legacyMemberService.currentMemberId(), category, count, cursor, "/api/1/members/me/likes", broker);
    }

    @GetMapping(value = "/{id:.+}/likes")
    public CursorResponse getMemberLikes(@PathVariable Long id,
                                         @RequestParam String category,
                                         @RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) Long cursor,
                                         @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
        return createLikeResponse(id, category, count, cursor, String.format("/api/1/members/%d/likes", id), null);
    }

    @GetMapping(value = "/me/videos")
    public CursorResponse getMyVideos(@RequestParam(defaultValue = "20") int count,
                                      @RequestParam(required = false) String cursor,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(required = false) String state) {
        Slice<Video> list = legacyVideoService.findMyVideos(legacyMemberService.currentMember(), type, state, cursor, count);
        List<LegacyVideoController.VideoInfo> videos = new ArrayList<>();
        list.stream().forEach(v -> videos.add(legacyVideoService.generateVideoInfo(v)));

        String nextCursor = null;
        if (videos.size() > 0) {
            nextCursor = String.valueOf(videos.get(videos.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/me/videos", videos)
                .withType(type)
                .withState(state)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping(value = "/{id:.+}/videos")
    public CursorResponse getMemberVideos(@PathVariable Long id,
                                          @RequestParam(defaultValue = "20") int count,
                                          @RequestParam(required = false) String cursor,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(required = false) String state,
                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        Slice<Video> list = legacyVideoService.findMemberVideos(member, type, state, cursor, count);
        List<LegacyVideoController.VideoInfo> videos = new ArrayList<>();
        list.stream().forEach(v -> videos.add(legacyVideoService.generateVideoInfo(v)));

        String nextCursor = null;
        if (videos.size() > 0) {
            nextCursor = String.valueOf(videos.get(videos.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/" + id + "/videos", videos)
                .withType(type)
                .withState(state)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping(value = "/me/members")
    public CursorResponse getMyMembers(@RequestParam(required = false) String keyword) {
        long me = legacyMemberService.currentMemberId();
        PageRequest pageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());

        LinkedHashSet<Member> members = new LinkedHashSet<>();
        List<MemberInfo> info = new ArrayList<>();

        if (StringUtils.isNotEmpty(keyword)) {
            for (Following f : followingRepository.findByMemberMeIdAndMemberYouUsernameStartingWith(me, keyword, pageable)) {
                members.add(f.getMemberYou());
            }
            for (Following f : followingRepository.findByMemberYouIdAndMemberMeUsernameStartingWith(me, keyword, pageable)) {
                members.add(f.getMemberMe());
            }

            for (Member member : members) {
                info.add(legacyMemberService.getMemberInfo(member));
            }
        } else {
            for (Following f : followingRepository.findByMemberMeId(me, pageable)) {
                members.add(f.getMemberYou());
            }
            for (Following f : followingRepository.findByMemberYouId(me, pageable)) {
                members.add(f.getMemberMe());
            }

            for (Member member : members) {
                info.add(legacyMemberService.getMemberInfo(member));
            }
        }

        return new CursorResponse.Builder<>(null, info).toBuild();
    }

    @Deprecated
    @DeleteMapping("/me")
    public ResponseEntity deleteMe(@Valid @RequestBody DeleteMemberRequest request,
                                   @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(legacyMemberService.currentMemberId())
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        legacyMemberService.deleteMember(request, member);
        legacyNotificationService.readAllNotification(member.getId());

        // Async processing after response
        postProcessService.deleteMember(member);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/me/delete")
    public ResponseEntity deleteMember(@Valid @RequestBody DeleteMemberRequest request,
                                       @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(legacyMemberService.currentMemberId())
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        legacyMemberService.deleteMember(request, member);
        legacyNotificationService.readAllNotification(member.getId());

        // Async processing after response
        postProcessService.deleteMember(member);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/me/comments")
    public CursorResponse getMyComments(@RequestParam(defaultValue = "100") int count,
                                        @RequestParam(required = false) String cursor,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        PageRequest pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        Long me = legacyMemberService.currentMemberId();
        Slice<Comment> comments;
        if (StringUtils.isNumeric(cursor)) {
            Date createdAt = new Date(Long.parseLong(cursor));
            comments = commentRepository.findByCreatedByIdAndCreatedAtBeforeAndParentIdIsNull(me, createdAt, pageable);
        } else {
            comments = commentRepository.findByCreatedByIdAndParentIdIsNull(me, pageable);
        }

        List<CommentInfo> result = transformComment(comments, me, lang);
        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/me/comments", result)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    private List<CommentInfo> transformComment(Slice<Comment> comments, Long me, String lang) {
        List<CommentInfo> result = new ArrayList<>();
        comments.stream().forEach(comment -> {
            CommentInfo commentInfo;
            if (comment.getComment().contains("@")) {
                MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
                if (mentionResult != null) {
                    String content = commentService.getBlindContent(comment, lang, mentionResult.getComment());
                    comment.setComment(content);
                    commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
                } else {
                    log.warn("mention result not found - {}", comment);
                    commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()));
                }
            } else {
                String content = commentService.getBlindContent(comment, lang, null);
                comment.setComment(content);
                commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()));
            }

            if (me != null) {
                Long likeId = commentLikeRepository.findByCommentIdAndCreatedByIdAndStatus(comment.getId(), me, LIKE)
                        .map(CommentLike::getId).orElse(null);
                commentInfo.setLikeId(likeId);
            }
            result.add(commentInfo);
        });

        return result;
    }

    @GetMapping(value = "/{id:.+}/comments")
    public CursorResponse getMemberComments(@PathVariable Long id,
                                            @RequestParam(defaultValue = "100") int count,
                                            @RequestParam(required = false) String cursor,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        PageRequest pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        Slice<Comment> comments;
        if (StringUtils.isNumeric(cursor)) {
            Date createdAt = new Date(Long.parseLong(cursor));
            comments = commentRepository.findByCreatedByIdAndCreatedAtBeforeAndParentIdIsNull(member.getId(), createdAt, pageable);
        } else {
            comments = commentRepository.findByCreatedByIdAndParentIdIsNull(member.getId(), pageable);
        }

        List<CommentInfo> result = transformComment(comments, member.getId(), lang);
        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/" + id + "/comments", result)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping("/me/revenues")
    public CursorResponse getRevenueSummaries(@RequestParam(defaultValue = "12") int count,
                                              @RequestParam(required = false) String cursor) {
        Member member = legacyMemberService.currentMember();

        PageRequest pageable = PageRequest.of(0, count, Sort.by("date").descending());
        Slice<RevenuePayment> list;

        if (StringUtils.isNotEmpty(cursor)) {
            list = revenuePaymentRepository.findByMemberAndDateLessThanEqual(member, cursor, pageable);
        } else {
            list = revenuePaymentRepository.findByMember(member, pageable);
        }

        List<RevenuePaymentInfo> revenues = new ArrayList<>();
        list.forEach(r -> revenues.add(new RevenuePaymentInfo(r)));


        legacyMemberService.readMemberRevenue(member);

        String nextCursor = null;
        if (revenues.size() > 0) {
            String date = revenues.get(revenues.size() - 1).getDate() + "-01";
            nextCursor = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).minusMonths(1).toString();
        }

        return new CursorResponse.Builder<>("/api/1/members/me/revenues", revenues)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping("/me/revenues/{revenuePaymentId:.+}/details")
    public CursorResponse getRevenueDetails(@PathVariable Long revenuePaymentId,
                                            @RequestParam(defaultValue = "100") int count,
                                            @RequestParam(defaultValue = "0") long cursor) {
        RevenuePayment revenuePayment = revenuePaymentRepository.findById(revenuePaymentId)
                .orElseThrow(() -> new NotFoundException("RevenuePayment not found: " + revenuePaymentId));

        PageRequest pageable = PageRequest.of(0, count, Sort.by("id").ascending());
        Slice<Revenue> list = revenueRepository.findByRevenuePaymentAndConfirmedIsTrueAndIdGreaterThanEqual(revenuePayment, cursor, pageable);

        List<RevenueInfo> revenues = new ArrayList<>();
        list.forEach(r -> revenues.add(new RevenueInfo(r)));

        String nextCursor = null;
        if (revenues.size() > 0) {
            nextCursor = String.valueOf(revenues.get(revenues.size() - 1).getId() + 1);
        }

        return new CursorResponse.Builder<>("/api/1/members/me/revenues/" + revenuePaymentId + "/details", revenues)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping(value = "/me/scraps")
    public CursorResponse getMyScraps(@RequestParam(defaultValue = "20") int count,
                                      @RequestParam(required = false) String cursor) {
        PageRequest pageRequest = PageRequest.of(0, count, Sort.by("id").descending());

        log.debug("count: {}, cursor: {}", count, cursor);
        Long memberId = legacyMemberService.currentMemberId();
        List<VideoScrap> list = legacyVideoScrapService.findByMemberId(memberId, cursor, Visibility.PUBLIC, pageRequest);
        List<LegacyVideoController.VideoScrapInfo> scraps = new ArrayList<>();
        list.stream().forEach(scrap -> scraps.add(
                new LegacyVideoController.VideoScrapInfo(scrap, legacyVideoService.generateVideoInfo(scrap.getVideo()))));

        String nextCursor = null;
        if (scraps.size() > 0) {
            nextCursor = String.valueOf(scraps.get(scraps.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/members/me/scraps", scraps)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }


    private CursorResponse createLikeResponse(Long memberId, String category, int count, Long cursor, String uri, Long broker) {
        switch (category) {
//            case "post":
//                return createPostLikeResponse(memberId, category, count, cursor, uri);
            case "goods":
                return createGoodsLikeResponse(memberId, category, count, cursor, uri, TimeSaleCondition.createWithBroker(broker));
            case "store":
                return createStoreLikeResponse(memberId, category, count, cursor, uri);
            case "video":
                return createVideoLikeResponse(memberId, category, count, cursor, uri);
            default:
                throw new BadRequestException("invalid category: " + category);
        }
    }

//    private CursorResponse createPostLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
//        PageRequest pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
//        Slice<PostLike> postLikes;
//        List<PostController.PostLikeInfo> result = new ArrayList<>();
//
//        if (cursor != null) {
//            postLikes = postLikeRepository.findByCreatedAtBeforeAndCreatedByIdAndPostDeletedAtIsNull(new Date(cursor), memberId, pageable);
//        } else {
//            postLikes = postLikeRepository.findByCreatedByIdAndPostDeletedAtIsNull(memberId, pageable);
//        }
//
//        postLikes.stream().forEach(like -> {
//            PostController.PostLikeInfo info = new PostController.PostLikeInfo(like,
//                    legacyMemberService.getMemberInfo(like.getCreatedBy()), legacyMemberService.getMemberInfo(like.getPost().getCreatedBy()));
//            postLikeRepository.findByPostIdAndCreatedById(like.getPost().getId(), memberId)
//                    .ifPresent(likeByMe -> info.getPost().setLikeId(likeByMe.getId()));
//            result.add(info);
//        });
//
//        String nextCursor = null;
//        if (result.size() > 0) {
//            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
//        }
//
//        return new CursorResponse.Builder<>(uri, result)
//                .withCategory(category)
//                .withCursor(nextCursor)
//                .withCount(count).toBuild();
//    }

    private CursorResponse createGoodsLikeResponse(Long memberId, String category, int count, Long cursor, String uri, TimeSaleCondition timeSaleCondition) {
        PageRequest pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        Slice<GoodsLike> goodsLikes;
        List<GoodsInfo> result = new ArrayList<>();

        if (cursor != null) {
            goodsLikes = goodsLikeRepository.findByCreatedAtBeforeAndCreatedById(
                    new Date(cursor), memberId, pageable);
        } else {
            goodsLikes = goodsLikeRepository.findByCreatedById(memberId, pageable);
        }

        for (GoodsLike like : goodsLikes) {
            result.add(goodsService.generateGoodsInfo(like.getGoods(), timeSaleCondition));
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(goodsLikes.getContent().get(goodsLikes.getContent().size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>(uri, result)
                .withCategory(category)
                .withCursor(nextCursor)
                .withCount(count).toBuild();
    }

    private CursorResponse createStoreLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
        PageRequest pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        Slice<StoreLike> storeLikes;
        List<StoreController.StoreInfo> result = new ArrayList<>();

        if (cursor != null) {
            storeLikes = storeLikeRepository.findByCreatedAtBeforeAndCreatedById(new Date(cursor), memberId, pageable);
        } else {
            storeLikes = storeLikeRepository.findByCreatedById(memberId, pageable);
        }

        Long likeId = null;
        Long me = legacyMemberService.currentMemberId();
        for (StoreLike like : storeLikes) {
            if (me != null) {
                likeId = me.equals(like.getCreatedBy().getId()) ? like.getId() : null;
            }
            result.add(new StoreController.StoreInfo(like.getStore(), likeId));
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(storeLikes.getContent().get(storeLikes.getContent().size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>(uri, result)
                .withCategory(category)
                .withCursor(nextCursor)
                .withCount(count).toBuild();
    }

    private CursorResponse createVideoLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
        PageRequest pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        Slice<VideoLike> videoLikes;
        List<LegacyVideoController.VideoInfo> result = new ArrayList<>();

        if (cursor != null) {
            videoLikes = videoLikeRepository.findByCreatedAtBeforeAndCreatedByIdAndVideoDeletedAtIsNullAndStatus(
                    new Date(cursor), memberId, pageable, LIKE);
        } else {
            videoLikes = videoLikeRepository.findByCreatedByIdAndVideoDeletedAtIsNullAndStatus(memberId, pageable, LIKE);
        }

        for (VideoLike like : videoLikes) {
            result.add(legacyVideoService.generateVideoInfo(like.getVideo()));
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(videoLikes.getContent().get(videoLikes.getContent().size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>(uri, result)
                .withCategory(category)
                .withCursor(nextCursor)
                .withCount(count).toBuild();
    }

    @NoArgsConstructor
    @Data
    public static class UpdateMemberRequest {

        private String username;

        private String avatarUrl;
    }

    @Data
    private static class LikeCountResponse {
        private Integer video = 0;
        private Integer goods = 0;
        private Integer store = 0;
        private Integer post = 0;
    }

    @NoArgsConstructor
    @Data
    public static class DeleteMemberRequest {

        @NotNull
        @Size(max = 255)
        private String reason;
    }

    @AllArgsConstructor
    @Data
    class Cursor<T> {
        private String keyword;
        private T cursor;
        private int count = 20;

        public boolean hasKeyword() {
            return !StringUtils.isBlank(keyword);
        }

        public boolean hasCursor() {
            return cursor != null;
        }
    }
}
