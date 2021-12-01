package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.video.scrap.VideoScrap;
import com.jocoos.mybeautip.video.scrap.VideoScrapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberMeInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.PostProcessService;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentInfo;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.revenue.Revenue;
import com.jocoos.mybeautip.member.revenue.RevenueInfo;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentInfo;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentRepository;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.search.KeywordService;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {
  
  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final PostProcessService postProcessService;
  private final MessageService messageService;
  private final MentionService mentionService;
  private final KeywordService keywordService;
  private final NotificationService notificationService;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;
  private final PostLikeRepository postLikeRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final StoreLikeRepository storeLikeRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final RevenueRepository revenueRepository;
  private final RevenuePaymentRepository revenuePaymentRepository;
  private final VideoScrapService videoScrapService;
  private final ReportRepository reportRepository;
  private final BlockRepository blockRepository;

  @Value("${mybeautip.store.image-path.prefix}")
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

  private static final String MEMBER_NOT_FOUND = "member.not_found";

  public MemberController(MemberService memberService,
                          GoodsService goodsService,
                          VideoService videoService,
                          MemberRepository memberRepository,
                          FollowingRepository followingRepository,
                          PostLikeRepository postLikeRepository,
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
                          NotificationService notificationService,
                          RevenuePaymentRepository revenuePaymentRepository,
                          VideoScrapService videoScrapService,
                          ReportRepository reportRepository,
                          BlockRepository blockRepository) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.videoService = videoService;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
    this.postLikeRepository = postLikeRepository;
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
    this.notificationService = notificationService;
    this.revenuePaymentRepository = revenuePaymentRepository;
    this.videoScrapService = videoScrapService;
    this.reportRepository = reportRepository;
    this.blockRepository = blockRepository;
  }

  @GetMapping("/me")
  public Resource<MemberMeInfo> getMe(Principal principal,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.debug("member id: {}", principal.getName());
    return memberRepository.findByIdAndDeletedAtIsNull(Long.parseLong(principal.getName()))
              .map(m -> new Resource<>(new MemberMeInfo(m, pointRatio, revenueRatio)))
              .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
  }
  
  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MemberInfo> updateMember(@Valid @RequestBody UpdateMemberRequest updateMemberRequest,
                                                 @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.debug("member id: {}", memberService.currentMemberId());
  
    Member member = memberRepository.findByIdAndDeletedAtIsNull(memberService.currentMemberId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    if (!member.isVisible()) { // when first called
      if (StringUtils.isEmpty(updateMemberRequest.getUsername())) {
        throw new BadRequestException("username_required", "Username required.");
      }
    }
    
    if (updateMemberRequest.getUsername() != null) {
      memberService.checkUsernameValidation(updateMemberRequest.getUsername(), lang);
    }

    if (updateMemberRequest.getEmail() != null) {
      memberService.checkEmailValidation(updateMemberRequest.getEmail(), lang);
    }
    
    member = memberService.updateMember(updateMemberRequest, member);
    return new ResponseEntity<>(memberService.getMemberInfo(member), HttpStatus.OK);
  }
  
  @GetMapping
  @ResponseBody
  public CursorResponse getMembers(@RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false) String cursor,
                                   @RequestParam(required = false) String keyword,
                                   @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    List<MemberInfo> members = Lists.newArrayList();
    String nextCursor = null;
    
    if (StringUtils.isNotBlank(keyword)) {
      keyword = keyword.trim();
    }

    if (keyword == null || (!StringUtils.containsWhitespace(keyword) && StringUtils.isNotEmpty(keyword))) {
      Slice<Member> list = findMembers(keyword, cursor, count);
      list.stream().forEach(m -> members.add(memberService.getMemberInfo(m)));

      if (members.size() > 0) {
        nextCursor = String.valueOf(members.get(members.size() - 1).getCreatedAt().getTime());
      }
    }
  
    if (StringUtils.isNotBlank(keyword)) {
      keyword = keyword.trim();
      try {
        keywordService.updateKeywordCount(keyword);
        keywordService.logHistory(keyword, KeywordService.KeywordCategory.MEMBER, memberService.currentMember());
      } catch (ConcurrencyFailureException e) { // Ignore
        log.warn("getMembers throws ConcurrencyFailureException: " + keyword);
      }
    }

    return new CursorResponse.Builder<>("/api/1/members", members)
       .withKeyword(keyword)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  @AllArgsConstructor
  @Data
  class Cursor<T> {
    private String keyword;
    private T cursor;
    private int count = 20;

    public boolean hasKeyword() {
      return !Strings.isNullOrEmpty(keyword);
    }

    public boolean hasCursor() {
      return cursor != null;
    }
  }

  // TODO: Refactor find member to MemberService
  private Slice<Member> findMembers(String keyword, String cursor, int count) {
    Cursor<Date> dateCursor;

    if (Strings.isNullOrEmpty(cursor)) {
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

    PageRequest page = PageRequest.of(0, cursor.getCount(), new Sort(Sort.Direction.DESC, "createdAt"));

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
                              @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return memberRepository.findByIdAndDeletedAtIsNull(id).map(memberService::getMemberInfo)
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
  }

  @GetMapping(value = "/me/like_count")
  public LikeCountResponse getMyLikesCount() {
    LikeCountResponse response = new LikeCountResponse();
    response.setGoods(goodsLikeRepository.countByCreatedById(memberService.currentMemberId()));
    response.setStore(storeLikeRepository.countByCreatedById(memberService.currentMemberId()));
    response.setPost(postLikeRepository.countByCreatedByIdAndPostDeletedAtIsNull(memberService.currentMemberId()));
    response.setVideo(videoLikeRepository.countByCreatedByIdAndVideoDeletedAtIsNull(memberService.currentMemberId()));
    return response;
  }

  @GetMapping(value = "/{id:.+}/like_count")
  public LikeCountResponse getMemberLikesCount(@PathVariable Long id,
                                               @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    LikeCountResponse response = new LikeCountResponse();
    response.setGoods(goodsLikeRepository.countByCreatedById(id));
    response.setStore(storeLikeRepository.countByCreatedById(id));
    response.setPost(postLikeRepository.countByCreatedByIdAndPostDeletedAtIsNull(id));
    response.setVideo(videoLikeRepository.countByCreatedByIdAndVideoDeletedAtIsNull(id));
    return response;
  }

  @GetMapping(value = "/me/likes")
  public CursorResponse getMyLikes(@RequestParam String category,
                                   @RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false) Long cursor,
                                   @RequestParam(name = "broker", required = false) Long broker) {
    return createLikeResponse(memberService.currentMemberId(), category, count, cursor, "/api/1/members/me/likes", broker);
  }

  @GetMapping(value = "/{id:.+}/likes")
  public CursorResponse getMemberLikes(@PathVariable Long id,
                                       @RequestParam String category,
                                       @RequestParam(defaultValue = "20") int count,
                                       @RequestParam(required = false) Long cursor,
                                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    return createLikeResponse(id, category, count, cursor, String.format("/api/1/members/%d/likes", id), null);
  }

  @GetMapping(value = "/me/videos")
  public CursorResponse getMyVideos(@RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) String cursor,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String state) {
    Slice<Video> list = videoService.findMyVideos(memberService.currentMember(), type, state, cursor, count);
    List<VideoController.VideoInfo> videos = Lists.newArrayList();
    list.stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));

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
                                        @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

    Slice<Video> list = videoService.findMemberVideos(member, type, state, cursor, count);
    List<VideoController.VideoInfo> videos = Lists.newArrayList();
    list.stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));

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
    long me = memberService.currentMemberId();
    PageRequest pageable = PageRequest.of(0, 100, new Sort(Sort.Direction.DESC, "createdAt"));
    
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
        info.add(memberService.getMemberInfo(member));
      }
    } else {
      for (Following f : followingRepository.findByMemberMeId(me, pageable)) {
        members.add(f.getMemberYou());
      }
      for (Following f : followingRepository.findByMemberYouId(me, pageable)) {
        members.add(f.getMemberMe());
      }
  
      for (Member member : members) {
        info.add(memberService.getMemberInfo(member));
      }
    }
    
    return new CursorResponse.Builder<>(null, info).toBuild();
  }
  
  @Deprecated
  @DeleteMapping("/me")
  public ResponseEntity deleteMe(@Valid @RequestBody DeleteMemberRequest request,
                                 @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberRepository.findByIdAndDeletedAtIsNull(memberService.currentMemberId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    memberService.deleteMember(request, member);
    notificationService.readAllNotification(member.getId());
    
    // Async processing after response
    postProcessService.deleteMember(member);
    return new ResponseEntity(HttpStatus.OK);
  }
  
  @PutMapping("/me/delete")
  public ResponseEntity deleteMember(@Valid @RequestBody DeleteMemberRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberRepository.findByIdAndDeletedAtIsNull(memberService.currentMemberId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    memberService.deleteMember(request, member);
    notificationService.readAllNotification(member.getId());
    
    // Async processing after response
    postProcessService.deleteMember(member);
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping(value = "/me/comments")
  public CursorResponse getMyComments(@RequestParam(defaultValue = "100") int count,
                                      @RequestParam(required = false) String cursor) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Long me = memberService.currentMemberId();
    Slice<Comment> comments;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = commentRepository.findByCreatedByIdAndCreatedAtBeforeAndParentIdIsNull(me, createdAt, pageable);
    } else {
      comments = commentRepository.findByCreatedByIdAndParentIdIsNull(me, pageable);
    }

    List<CommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
      CommentInfo commentInfo;
      if (comment.getComment().contains("@")) {
        MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
        if (mentionResult != null) {
          comment.setComment(mentionResult.getComment());
          commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
        } else {
          log.warn("mention result not found - {}", comment);
          commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()));
        }
      } else {
        commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()));
      }
      
      if (me != null) {
        Long likeId = commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), me)
            .map(CommentLike::getId).orElse(null);
        commentInfo.setLikeId(likeId);
      }
      result.add(commentInfo);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/comments", result)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }
  
  @GetMapping(value = "/{id:.+}/comments")
  public CursorResponse getMemberComments(@PathVariable Long id,
                                          @RequestParam(defaultValue = "100") int count,
                                          @RequestParam(required = false) String cursor,
                                          @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<Comment> comments;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = commentRepository.findByCreatedByIdAndCreatedAtBeforeAndParentIdIsNull(member.getId(), createdAt, pageable);
    } else {
      comments = commentRepository.findByCreatedByIdAndParentIdIsNull(member.getId(), pageable);
    }
    
    List<CommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
      CommentInfo commentInfo;
      if (comment.getComment().contains("@")) {
        MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
        if (mentionResult != null) {
          comment.setComment(mentionResult.getComment());
          commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
        } else {
          log.warn("mention result not found - {}", comment);
          commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()));
        }
      } else {
        commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()));
      }
      
      Long me = memberService.currentMemberId();
      if (me != null) {
        Long likeId = commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), me)
            .map(CommentLike::getId).orElse(null);
        commentInfo.setLikeId(likeId);
      }
      result.add(commentInfo);
    });
    
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
    Member member = memberService.currentMember();
    
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "date"));
    Slice<RevenuePayment>  list;

    if (StringUtils.isNotEmpty(cursor)) {
      list = revenuePaymentRepository.findByMemberAndDateLessThanEqual(member, cursor, pageable);
    } else {
      list = revenuePaymentRepository.findByMember(member, pageable);
    }
    
    List<RevenuePaymentInfo> revenues = Lists.newArrayList();
    list.forEach(r -> revenues.add(new RevenuePaymentInfo(r)));
  
    
    memberService.readMemberRevenue(member);
    
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
        .orElseThrow(() -> new NotFoundException("revenue_payment_not_found", "RevenuePayment not found: " + revenuePaymentId));
    
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    Slice<Revenue> list = revenueRepository.findByRevenuePaymentAndConfirmedIsTrueAndIdGreaterThanEqual(revenuePayment, cursor, pageable);
    
    List<RevenueInfo> revenues = Lists.newArrayList();
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
    PageRequest pageRequest = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id"));

    log.debug("count: {}, cursor: {}", count, cursor);
    Long memberId = memberService.currentMemberId();
    List<VideoScrap> list = videoScrapService.findByMemberId(memberId, cursor, pageRequest);
    List<VideoController.VideoScrapInfo> scraps = Lists.newArrayList();
    list.stream().forEach(scrap -> scraps.add(
        new VideoController.VideoScrapInfo(scrap, videoService.generateVideoInfo(scrap.getVideo()))));

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
      case "post":
        return createPostLikeResponse(memberId, category, count, cursor, uri);
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

  private CursorResponse createPostLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<PostLike> postLikes;
    List<PostController.PostLikeInfo> result = Lists.newArrayList();

    if (cursor != null) {
      postLikes = postLikeRepository.findByCreatedAtBeforeAndCreatedByIdAndPostDeletedAtIsNull(new Date(cursor), memberId, pageable);
    } else {
      postLikes = postLikeRepository.findByCreatedByIdAndPostDeletedAtIsNull(memberId, pageable);
    }

    postLikes.stream().forEach(like -> {
      PostController.PostLikeInfo info = new PostController.PostLikeInfo(like,
          memberService.getMemberInfo(like.getCreatedBy()), memberService.getMemberInfo(like.getPost().getCreatedBy()));
      postLikeRepository.findByPostIdAndCreatedById(like.getPost().getId(), memberId)
        .ifPresent(likeByMe -> info.getPost().setLikeId(likeByMe.getId()));
      result.add(info);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>(uri, result)
       .withCategory(category)
       .withCursor(nextCursor)
       .withCount(count).toBuild();
  }

  private CursorResponse createGoodsLikeResponse(Long memberId, String category, int count, Long cursor, String uri, TimeSaleCondition timeSaleCondition) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<GoodsLike> goodsLikes;
    List<GoodsInfo> result = Lists.newArrayList();

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
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<StoreLike> storeLikes;
    List<StoreController.StoreInfo> result = Lists.newArrayList();

    if (cursor != null) {
      storeLikes = storeLikeRepository.findByCreatedAtBeforeAndCreatedById(new Date(cursor), memberId, pageable);
    } else {
      storeLikes = storeLikeRepository.findByCreatedById(memberId, pageable);
    }

    Long likeId = null;
    Long me = memberService.currentMemberId();
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
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<VideoLike> videoLikes;
    List<VideoController.VideoInfo> result = Lists.newArrayList();

    if (cursor != null) {
      videoLikes = videoLikeRepository.findByCreatedAtBeforeAndCreatedByIdAndVideoDeletedAtIsNull(
        new Date(cursor), memberId, pageable);
    } else {
      videoLikes = videoLikeRepository.findByCreatedByIdAndVideoDeletedAtIsNull(memberId, pageable);
    }

    for (VideoLike like : videoLikes) {
      result.add(videoService.generateVideoInfo(like.getVideo()));
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

    @Size(max = 50)
    private String username;

    @Size(max = 50)
    private String email;

    @Size(max = 200)
    private String avatarUrl;

    @Size(max = 200)
    private String intro;
    
    private Boolean pushable;
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
}
