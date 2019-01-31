package com.jocoos.mybeautip.restapi;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jocoos.mybeautip.devices.DeviceRepository;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsLike;
import com.jocoos.mybeautip.goods.GoodsLikeRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.log.MemberLeaveLogRepository;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.member.comment.*;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.revenue.Revenue;
import com.jocoos.mybeautip.member.revenue.RevenueInfo;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.VideoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {
  
  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final TagService tagService;
  private final NotificationService notificationService;
  private final DeviceService deviceService;
  private final PostProcessService postProcessService;
  private final MessageService messageService;
  private final MentionService mentionService;
  private final MemberRepository memberRepository;
  private final FacebookMemberRepository facebookMemberRepository;
  private final NaverMemberRepository naverMemberRepository;
  private final KakaoMemberRepository kakaoMemberRepository;
  private final PostLikeRepository postLikeRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final StoreLikeRepository storeLikeRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final RevenueRepository revenueRepository;
  private final MemberLeaveLogRepository memberLeaveLogRepository;
  private final DeviceRepository deviceRepository;

  @Value("${mybeautip.store.image-path.prefix}")
  private String storeImagePrefix;

  @Value("${mybeautip.store.image-path.cover-suffix}")
  private String storeImageSuffix;

  @Value("${mybeautip.store.image-path.thumbnail-suffix}")
  private String storeImageThumbnailSuffix;

  @Value("${mybeautip.point.earn-ratio}")
  private int pointRatio;

  @Value("${mybeautip.revenue.revenue-ratio}")
  private int revenueRatio;

  private static final String MEMBER_NOT_FOUND = "member.not_found";
  private final String defaultAvatarUrl = "https://s3.ap-northeast-2.amazonaws.com/mybeautip/avatar/img_profile_default.png";

  public MemberController(MemberService memberService,
                          GoodsService goodsService,
                          VideoService videoService,
                          TagService tagService,
                          MemberRepository memberRepository,
                          FacebookMemberRepository facebookMemberRepository,
                          NaverMemberRepository naverMemberRepository,
                          KakaoMemberRepository kakaoMemberRepository,
                          PostLikeRepository postLikeRepository,
                          GoodsLikeRepository goodsLikeRepository,
                          StoreLikeRepository storeLikeRepository,
                          VideoLikeRepository videoLikeRepository,
                          CommentRepository commentRepository,
                          CommentLikeRepository commentLikeRepository,
                          RevenueRepository revenueRepository,
                          MemberLeaveLogRepository memberLeaveLogRepository,
                          NotificationService notificationService,
                          DeviceService deviceService,
                          PostProcessService postProcessService,
                          MessageService messageService,
                          DeviceRepository deviceRepository,
                          MentionService mentionService) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.videoService = videoService;
    this.tagService = tagService;
    this.memberRepository = memberRepository;
    this.facebookMemberRepository = facebookMemberRepository;
    this.naverMemberRepository = naverMemberRepository;
    this.kakaoMemberRepository = kakaoMemberRepository;
    this.postLikeRepository = postLikeRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.storeLikeRepository = storeLikeRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.commentRepository = commentRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.revenueRepository = revenueRepository;
    this.memberLeaveLogRepository = memberLeaveLogRepository;
    this.notificationService = notificationService;
    this.deviceService = deviceService;
    this.postProcessService = postProcessService;
    this.messageService = messageService;
    this.deviceRepository = deviceRepository;
    this.mentionService = mentionService;
  }

  @GetMapping("/me")
  public Resource<MemberMeInfo> getMe(Principal principal,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.debug("member id: {}", principal.getName());
    return memberRepository.findByIdAndDeletedAtIsNull(Long.parseLong(principal.getName()))
              .map(m -> new Resource<>(new MemberMeInfo(m, pointRatio, revenueRatio)))
              .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
  }

  @Transactional
  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MemberInfo> updateMember(@Valid @RequestBody UpdateMemberRequest updateMemberRequest,
                                                 @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.debug("member id: {}", memberService.currentMemberId());
    
    if (updateMemberRequest.getUsername() != null) {
      memberService.checkUsernameValidation(updateMemberRequest.getUsername(), lang);
    }

    if (updateMemberRequest.getEmail() != null) {
      memberService.checkEmailValidation(updateMemberRequest.getEmail(), lang);
    }

    return memberRepository.findByIdAndDeletedAtIsNull(memberService.currentMemberId())
        .map(m -> {
          if (updateMemberRequest.getUsername() != null) {
            m.setUsername(updateMemberRequest.getUsername());
          }
          if (updateMemberRequest.getEmail() != null) {
            m.setEmail(updateMemberRequest.getEmail());
          }
          if (updateMemberRequest.getAvatarUrl() != null) {
            if ("".equals(updateMemberRequest.getAvatarUrl())) {
              m.setAvatarUrl(defaultAvatarUrl);
            } else {
              m.setAvatarUrl(updateMemberRequest.getAvatarUrl());
            }
          }
          if (updateMemberRequest.getIntro() != null) {
            tagService.touchRefCount(updateMemberRequest.getIntro());
            tagService.updateHistory(m.getIntro(), updateMemberRequest.getIntro(), TagService.TAG_MEMBER, m.getId(), m);
            m.setIntro(updateMemberRequest.getIntro());
          }
  
          if (updateMemberRequest.getPushable() != null) {
            if (!m.getPushable().equals(updateMemberRequest.getPushable())) {
              deviceRepository.findByCreatedByIdAndValidIsTrue(m.getId()).forEach(
                  device -> {
                    device.setPushable(updateMemberRequest.getPushable());
                    deviceRepository.save(device);
                  }
              );
            }
            m.setPushable(updateMemberRequest.getPushable());
          }
  
          m.setVisible(true);
          memberRepository.save(m);

          return new ResponseEntity<>(memberService.getMemberInfo(m), HttpStatus.OK);
        })
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
  }
  
  @Transactional
  @GetMapping
  @ResponseBody
  public CursorResponse getMembers(@RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false) String cursor,
                                   @RequestParam(required = false) String keyword,
                                   @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    List<MemberInfo> members = Lists.newArrayList();
    String nextCursor = null;

    if (keyword == null || (!StringUtils.containsWhitespace(keyword) && StringUtils.isNotEmpty(keyword))) {
      Slice<Member> list = findMembers(keyword, cursor, count);
      list.stream().forEach(m -> members.add(memberService.getMemberInfo(m)));

      if (members.size() > 0) {
        nextCursor = String.valueOf(members.get(members.size() - 1).getCreatedAt().getTime());
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
                                   @RequestParam(required = false) Long cursor) {
    return createLikeResponse(memberService.currentMemberId(), category, count, cursor, "/api/1/members/me/likes");
  }

  @GetMapping(value = "/{id:.+}/likes")
  public CursorResponse getMemberLikes(@PathVariable Long id,
                                       @RequestParam String category,
                                       @RequestParam(defaultValue = "20") int count,
                                       @RequestParam(required = false) Long cursor,
                                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    return createLikeResponse(id, category, count, cursor, String.format("/api/1/members/%d/likes", id));
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

  @Deprecated
  @Transactional
  @DeleteMapping("/me")
  public void deleteMe(@Valid @RequestBody DeleteMemberRequest request,
                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberRepository.findByIdAndDeletedAtIsNull(memberService.currentMemberId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    int link = member.getLink();
    switch (link) {
      case 1:
        facebookMemberRepository.findByMemberId(member.getId()).ifPresent(facebookMemberRepository::delete);
        break;

      case 2:
        naverMemberRepository.findByMemberId(member.getId()).ifPresent(naverMemberRepository::delete);
        break;

      case 4:
        kakaoMemberRepository.findByMemberId(member.getId()).ifPresent(kakaoMemberRepository::delete);
        break;

      default:
        throw new BadRequestException("invalid_member_link", "invalid member link: " + link);
    }
    
    member.setIntro("");
    member.setAvatarUrl("https://s3.ap-northeast-2.amazonaws.com/mybeautip/avatar/img_profile_deleted.png");
    member.setVisible(false);
    member.setFollowingCount(0);
    member.setFollowerCount(0);
    member.setPublicVideoCount(0);
    member.setTotalVideoCount(0);
    member.setDeletedAt(new Date());
    memberRepository.saveAndFlush(member);
    
    log.debug(String.format("Member deleted: %d, %s, %s", member.getId(), member.getUsername(), member.getDeletedAt()));
    
    // Sync processing before response
    notificationService.readAllNotification(member.getId());
    deviceService.disableAllDevices(member.getId());
    memberLeaveLogRepository.save(new MemberLeaveLog(member, request.getReason()));
    
    // Async processing after response
    postProcessService.deleteMember(member);
  }
  
  @Transactional
  @PutMapping("/me/delete")
  public void deleteMember(@Valid @RequestBody DeleteMemberRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberRepository.findByIdAndDeletedAtIsNull(memberService.currentMemberId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    int link = member.getLink();
    switch (link) {
      case 1:
        facebookMemberRepository.findByMemberId(member.getId()).ifPresent(facebookMemberRepository::delete);
        break;
      
      case 2:
        naverMemberRepository.findByMemberId(member.getId()).ifPresent(naverMemberRepository::delete);
        break;
      
      case 4:
        kakaoMemberRepository.findByMemberId(member.getId()).ifPresent(kakaoMemberRepository::delete);
        break;
      
      default:
        throw new BadRequestException("invalid_member_link", "invalid member link: " + link);
    }
    
    member.setIntro("");
    member.setAvatarUrl("https://s3.ap-northeast-2.amazonaws.com/mybeautip/avatar/img_profile_deleted.png");
    member.setVisible(false);
    member.setFollowingCount(0);
    member.setFollowerCount(0);
    member.setPublicVideoCount(0);
    member.setTotalVideoCount(0);
    member.setDeletedAt(new Date());
    memberRepository.saveAndFlush(member);
    
    log.debug(String.format("Member deleted: %d, %s, %s", member.getId(), member.getUsername(), member.getDeletedAt()));
    
    // Sync processing before response
    notificationService.readAllNotification(member.getId());
    deviceService.disableAllDevices(member.getId());
    memberLeaveLogRepository.save(new MemberLeaveLog(member, request.getReason()));
    
    // Async processing after response
    postProcessService.deleteMember(member);
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

  @Transactional
  @GetMapping("/me/revenues")
  public CursorResponse getRevenues(@RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) String cursor) {
    Member member = memberService.currentMember();
    member.setRevenueModifiedAt(null);
    memberRepository.save(member);
    
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    Slice<Revenue>  list;

    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      list = revenueRepository.findByVideoMemberAndCreatedAtBefore(member, createdAt, pageable);
    } else {
      list = revenueRepository.findByVideoMember(member, pageable);
    }

    List<RevenueInfo> revenues = Lists.newArrayList();

    list.forEach(r -> revenues.add(new RevenueInfo(r)));

    String nextCursor = null;
    if (revenues.size() > 0) {
      nextCursor = String.valueOf(revenues.get(revenues.size() - 1).getCreatedAt());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/revenues", revenues)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }


  private CursorResponse createLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    switch (category) {
      case "post":
        return createPostLikeResponse(memberId, category, count, cursor, uri);
      case "goods":
        return createGoodsLikeResponse(memberId, category, count, cursor, uri);
      case "store":
        return createStoreLikeResponse(memberId, category, count, cursor, uri);
      case "video":
        return createVideoLikeResponse(memberId, category, count, cursor, uri);
      default:
        throw new BadRequestException("invalid category: " + category);
    }
  }

  private CursorResponse createPostLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
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

  private CursorResponse createGoodsLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<GoodsLike> goodsLikes;
    List<GoodsInfo> result = Lists.newArrayList();

    if (cursor != null) {
      goodsLikes = goodsLikeRepository.findByCreatedAtBeforeAndCreatedById(
        new Date(cursor), memberId, pageable);
    } else {
      goodsLikes = goodsLikeRepository.findByCreatedById(memberId, pageable);
    }

    for (GoodsLike like : goodsLikes) {
      result.add(goodsService.generateGoodsInfo(like.getGoods()));
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
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
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
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
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
