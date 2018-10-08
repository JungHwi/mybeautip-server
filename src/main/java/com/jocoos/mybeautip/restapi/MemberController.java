package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsLike;
import com.jocoos.mybeautip.goods.GoodsLikeRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentInfo;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.revenue.Revenue;
import com.jocoos.mybeautip.member.revenue.RevenueInfo;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.VideoService;
import com.jocoos.mybeautip.word.BannedWordService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final MemberRepository memberRepository;
  private final PostLikeRepository postLikeRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final StoreLikeRepository storeLikeRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final BannedWordService bannedWordService;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final RevenueRepository revenueRepository;

  @Value("${mybeautip.store.image-path.prefix}")
  private String storeImagePrefix;

  @Value("${mybeautip.store.image-path.cover-suffix}")
  private String storeImageSuffix;

  @Value("${mybeautip.store.image-path.thumbnail-suffix}")
  private String storeImageThumbnailSuffix;

  public MemberController(MemberService memberService,
                          GoodsService goodsService,
                          VideoService videoService,
                          MemberRepository memberRepository,
                          PostLikeRepository postLikeRepository,
                          GoodsLikeRepository goodsLikeRepository,
                          StoreLikeRepository storeLikeRepository,
                          VideoLikeRepository videoLikeRepository,
                          BannedWordService bannedWordService,
                          CommentRepository commentRepository,
                          CommentLikeRepository commentLikeRepository,
                          RevenueRepository revenueRepository) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.videoService = videoService;
    this.memberRepository = memberRepository;
    this.postLikeRepository = postLikeRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.storeLikeRepository = storeLikeRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.bannedWordService = bannedWordService;
    this.commentRepository = commentRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.revenueRepository = revenueRepository;
  }

  @GetMapping("/me")
  public Resource<MemberMeInfo> getMe(Principal principal) {
    log.debug("member id: {}", principal.getName());
    return memberRepository.findById(Long.parseLong(principal.getName()))
              .map(m -> new Resource<>(new MemberMeInfo(m)))
              .orElseThrow(() -> new MemberNotFoundException("member_not_found"));
  }

  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MemberInfo> updateMember(Principal principal,
                                             @Valid @RequestBody UpdateMemberRequest updateMemberRequest,
                                             BindingResult bindingResult) {

    log.debug("member id: {}", principal.getName());
    log.debug("binding result: {}", bindingResult);

    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request");
    }

    // Validation check: username cannot be empty
    if ("".equals(updateMemberRequest.getUsername())) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request - username");
    }

    if (StringUtils.isNotEmpty(updateMemberRequest.getUsername())) {
      memberService.checkUsernameValidation(updateMemberRequest.getUsername());
    }

    // Validation check: email cannot be empty
    if ("".equals(updateMemberRequest.getEmail())) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request - email");
    }

    return memberRepository.findById(Long.parseLong(principal.getName()))
        .map(m -> {
          if (updateMemberRequest.getUsername() != null) {
            m.setUsername(validateUsername(updateMemberRequest.getUsername()));
            m.setVisible(true);
          }
          if (updateMemberRequest.getEmail() != null) {
            m.setEmail(updateMemberRequest.getEmail());
          }
          if (updateMemberRequest.getAvatarUrl() != null) {
            m.setAvatarUrl(updateMemberRequest.getAvatarUrl());
          }
          if (updateMemberRequest.getIntro() != null) {
            m.setIntro(updateMemberRequest.getIntro());
          }
          memberRepository.save(m);

          return new ResponseEntity<>(memberService.getMemberInfo(m), HttpStatus.OK);
        })
        .orElseThrow(() -> new MemberNotFoundException(principal.getName()));
  }

  private String validateUsername(String username) {
    if (memberRepository.countByUsernameAndDeletedAtIsNull(username) > 0) {
      throw new BadRequestException("duplicated_username", "Your username is already in use");
    }

    return bannedWordService.findWordAndThrowException(username);
  }

  @GetMapping
  @ResponseBody
  public CursorResponse getMembers(@RequestParam(defaultValue = "20") int count,
                                               @RequestParam(required = false) String cursor,
                                               @RequestParam(required = false) String keyword) {
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
    Cursor<Date> dateCursor = null;

    if (Strings.isNullOrEmpty(cursor)) {
      dateCursor = new Cursor<Date>(keyword, null, count);
    } else {
      Date toDate = null;
      try {
        toDate = new Date(Long.parseLong(cursor));
        dateCursor = new Cursor<Date>(keyword, toDate, count);
      } catch (NumberFormatException e) {
        log.error("Cannot convert cursor to Date", e);
        throw new BadRequestException("invalid cursor type");
      }
    }

    return findMembers(dateCursor);
  }

  private Slice<Member> findMembers(Cursor<Date> cursor) {
    Slice<Member> list = null;
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
  public MemberInfo getMember(@PathVariable Long id) {
    return memberRepository.findById(id).map(memberService::getMemberInfo)
        .orElseThrow(() -> new MemberNotFoundException(id));
  }

  @GetMapping(value = "/me/like_count")
  public LikeCountResponse getMyLikesCount() {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    LikeCountResponse response = new LikeCountResponse();
    response.setGoods(goodsLikeRepository.countByCreatedById(memberId));
    response.setStore(storeLikeRepository.countByCreatedById(memberId));
    response.setPost(postLikeRepository.countByCreatedById(memberId));
    response.setVideo(videoLikeRepository.countByCreatedById(memberId));
    return response;
  }

  @GetMapping(value = "/{id:.+}/like_count")
  public LikeCountResponse getMemberLikesCount(@PathVariable Long id) {
    LikeCountResponse response = new LikeCountResponse();
    response.setGoods(goodsLikeRepository.countByCreatedById(id));
    response.setStore(storeLikeRepository.countByCreatedById(id));
    response.setPost(postLikeRepository.countByCreatedById(id));
    response.setVideo(videoLikeRepository.countByCreatedById(id));
    return response;
  }

  @GetMapping(value = "/me/likes")
  public CursorResponse getMyLikes(@RequestParam String category,
                                   @RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false) Long cursor) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }
    return createLikeResponse(memberId, category, count, cursor, "/api/1/members/me/likes");
  }

  @GetMapping(value = "/{id:.+}/likes")
  public CursorResponse getMemberLikes(@PathVariable Long id,
                                       @RequestParam String category,
                                       @RequestParam(defaultValue = "20") int count,
                                       @RequestParam(required = false) Long cursor) {
    log.debug("id:{}, category:{}", id, category);
    return createLikeResponse(id, category, count, cursor, String.format("/api/1/members/%d/likes", id));
  }

  @GetMapping(value = "/me/videos")
  public CursorResponse getMyVideos(@RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) String cursor,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String state) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

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
                                        @RequestParam(required = false) String state) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new MemberNotFoundException(id));

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
      CommentInfo commentInfo = new CommentInfo(
        comment, memberService.getMemberInfo(comment.getCreatedBy()));
      if (me != null) {
        commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), me)
          .ifPresent(liked -> commentInfo.setLikeId(liked.getId()));
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

  @GetMapping("/me/revenues")
  public CursorResponse getRevenues(@RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) String cursor) {
    Member member = memberService.currentMember();
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    Slice<Revenue>  list = null;

    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      list = revenueRepository.findByVideoMemberAndCreatedAtBefore(member, createdAt, pageable);
    } else {
      list = revenueRepository.findByVideoMember(member, pageable);
    }

    List<RevenueInfo> revenues = Lists.newArrayList();

    list.forEach(r -> {
      revenues.add(new RevenueInfo(r));
    });

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
    Slice<PostLike> postLikes = null;
    List<PostController.PostLikeInfo> result = Lists.newArrayList();

    if (cursor != null) {
      postLikes = postLikeRepository.findByCreatedAtBeforeAndCreatedById(new Date(cursor), memberId, pageable);
    } else {
      postLikes = postLikeRepository.findByCreatedById(memberId, pageable);
    }

    postLikes.stream().forEach(like -> {
      PostController.PostLikeInfo info = new PostController.PostLikeInfo(like);
      postLikeRepository.findByPostIdAndCreatedById(like.getPost().getId(), memberId)
        .ifPresent(likeByMe -> info.getPost().setLikeId(likeByMe.getId()));
      result.add(info);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<PostController.PostLikeInfo>(uri, result)
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
      videoLikes = videoLikeRepository.findByCreatedAtBeforeAndCreatedById(
        new Date(cursor), memberId, pageable);
    } else {
      videoLikes = videoLikeRepository.findByCreatedById(memberId, pageable);
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
  }

  @Data
  private static class LikeCountResponse {
    private Integer video = 0;
    private Integer goods = 0;
    private Integer store = 0;
    private Integer post = 0;
  }
}
