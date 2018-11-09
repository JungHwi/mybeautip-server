package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.*;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.member.revenue.*;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.*;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;
import com.jocoos.mybeautip.video.view.VideoView;
import com.jocoos.mybeautip.video.view.VideoViewRepository;
import com.jocoos.mybeautip.video.watches.VideoWatch;
import com.jocoos.mybeautip.video.watches.VideoWatchRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/videos", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {
  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final VideoRepository videoRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final VideoWatchRepository videoWatchRepository;
  private final VideoReportRepository videoReportRepository;
  private final VideoViewRepository videoViewRepository;
  private final CommentService commentService;
  private final MentionService mentionService;
  private final RevenueService revenueService;
  private final TagService tagService;
  private final RevenueRepository revenueRepository;
  
  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;
  
  public VideoController(MemberService memberService,
                         VideoService videoService,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsService goodsService,
                         VideoRepository videoRepository,
                         VideoLikeRepository videoLikeRepository,
                         CommentRepository commentRepository,
                         CommentLikeRepository commentLikeRepository,
                         VideoWatchRepository videoWatchRepository,
                         VideoReportRepository videoReportRepository,
                         VideoViewRepository videoViewRepository,
                         CommentService commentService,
                         MentionService mentionService,
                         RevenueService revenueService,
                         TagService tagService,
                         RevenueRepository revenueRepository) {
    this.memberService = memberService;
    this.videoService = videoService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsService = goodsService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.videoWatchRepository = videoWatchRepository;
    this.videoReportRepository = videoReportRepository;
    this.videoViewRepository = videoViewRepository;
    this.commentService = commentService;
    this.mentionService = mentionService;
    this.revenueService = revenueService;
    this.tagService = tagService;
    this.revenueRepository = revenueRepository;
  }

  @GetMapping("{id}")
  public VideoInfo getVideos(@PathVariable Long id) {
    return videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(videoService::generateVideoInfo)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));
  }

  @GetMapping
  public CursorResponse getVideos(@RequestParam(defaultValue = "50") int count,
                                  @RequestParam(required = false) String cursor,
                                  @RequestParam(required = false) String type,
                                  @RequestParam(required = false) String state) {
    Slice<Video> list = videoService.findVideos(type, state, cursor, count);
    List<VideoInfo> videos = Lists.newArrayList();
    list.stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));

    String nextCursor = null;
    if (videos.size() > 0) {
      nextCursor = String.valueOf(videos.get(videos.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/videos", videos)
      .withType(type)
      .withState(state)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  @GetMapping("/search")
  public CursorResponse searchVideos(@RequestParam(defaultValue = "50") int count,
                                  @RequestParam(required = false) String cursor,
                                  @RequestParam String keyword) {
    Slice<Video> list = videoService.findVideosWithKeyword(keyword, cursor, count);
    List<VideoInfo> videos = Lists.newArrayList();
    list.stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));

    String nextCursor = null;
    if (videos.size() > 0) {
      nextCursor = String.valueOf(videos.get(videos.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/videos/search", videos)
      .withKeyword(keyword)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  @GetMapping("/{id}/goods")
  public List<GoodsInfo> getRelatedGoods(@PathVariable("id") Long id) {
    List<VideoGoods> list = videoGoodsRepository.findAllByVideoIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(id, "y");

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (VideoGoods video : list) {
      relatedGoods.add(goodsService.generateGoodsInfo(video.getGoods()));
    }
    return relatedGoods;
  }

  @GetMapping("/{id}/comments")
  public CursorResponse getComments(@PathVariable Long id,
                                    @RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) String cursor,
                                    @RequestParam(required = false) Long parentId) {
    PageRequest page = PageRequest.of(0, count);
    Slice<Comment> comments;
    Long me = memberService.currentMemberId();

    if (parentId != null) {
      comments = videoService.findCommentsByParentId(parentId, cursor, page);
    } else {
      comments = videoService.findCommentsByVideoId(id, cursor, page);
    }

    List<CommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
      CommentInfo commentInfo = null;
      if (comment.getComment().contains("@")) {
        MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
        if (mentionResult != null) {
          comment.setComment(mentionResult.getComment());
          commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
        } else {
          log.warn("mention result not found - {}", comment);
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

    int totalCount = videoRepository.findById(id).map(Video::getCommentCount).orElse(0);

    return new CursorResponse
      .Builder<>("/api/1/videos/" + id + "/comments", result)
      .withCount(count)
      .withCursor(nextCursor)
      .withTotalCount(totalCount).toBuild();
  }

  @Transactional
  @PostMapping("/{id:.+}/comments")
  public ResponseEntity addComment(@PathVariable Long id,
                                        @RequestBody CreateCommentRequest request,
                                        BindingResult bindingResult) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
    
    videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));

    if (request.getParentId() != null) {
      commentRepository.findById(request.getParentId())
        .map(parent -> {
          commentRepository.updateCommentCount(parent.getId(), 1);
          return Optional.empty();
        })
        .orElseThrow(() -> new NotFoundException("comment_id_not_found", "invalid comment parent id"));
    }

    Comment comment = new Comment();
    comment.setVideoId(id);
    BeanUtils.copyProperties(request, comment);
    
    tagService.parseHashTagsAndToucheRefCount(comment.getComment());
    videoRepository.updateCommentCount(id, 1);

    commentService.save(comment);

    List<MentionTag> mentionTags = request.getMentionTags();
    if (mentionTags != null && mentionTags.size() > 0) {
      mentionService.updateVideoCommentWithMention(comment, mentionTags);
    }

    return new ResponseEntity<>(
      new CommentInfo(comment),
      HttpStatus.OK
    );
  }

  @PatchMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity updateComment(@PathVariable Long videoId,
                                           @PathVariable Long id,
                                           @RequestBody VideoController.UpdateCommentRequest request,
                                           BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return commentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberId)
      .map(comment -> {
        comment.setComment(request.getComment());
        tagService.parseHashTagsAndToucheRefCount(comment.getComment());
        return new ResponseEntity<>(
          new CommentInfo(commentRepository.save(comment)),
          HttpStatus.OK
        );
      })
      .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video key id or comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeComment(@PathVariable Long videoId,
                                              @PathVariable Long id) {
    videoRepository.updateCommentCount(videoId, -1);

    Long memberId = memberService.currentMemberId();
    return commentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberId)
      .map(comment -> {
        if (comment.getParentId() != null) {
          commentRepository.updateCommentCount(comment.getParentId(), -1);
        }
        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(
            comment.getId());
        commentLikeRepository.deleteAll(commentLikes);
        commentRepository.delete(comment);
        return new ResponseEntity<>(HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video key or comment id"));
  }

  /**
   * Likes
   */
  @Transactional
  @PostMapping("/{videoId:.+}/likes")
  public ResponseEntity<VideoLikeInfo> addVideoLike(@PathVariable Long videoId) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return videoRepository.findByIdAndDeletedAtIsNull(videoId)
      .map(video -> {
        if (videoLikeRepository.findByVideoIdAndCreatedById(videoId, memberId).isPresent()) {
          throw new BadRequestException("duplicated_video_like", "Already video liked");
        }

        videoRepository.updateLikeCount(videoId, 1);
        video.setLikeCount(video.getLikeCount() + 1);
        VideoLike videoLike = videoLikeRepository.save(new VideoLike(video));
        VideoLikeInfo info = new VideoLikeInfo(videoLike, videoService.generateVideoInfo(video));
        return new ResponseEntity<>(info, HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id: " + videoId));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeVideoLike(@PathVariable Long videoId,
                                           @PathVariable Long likeId) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return videoLikeRepository.findByIdAndVideoIdAndCreatedById(likeId, videoId, memberId)
      .map(video -> {
        Optional<VideoLike> liked = videoLikeRepository.findById(likeId);
        if (!liked.isPresent()) {
          throw new NotFoundException("like_not_found", "invalid video like id");
        }

        videoLikeRepository.delete(liked.get());
        videoRepository.updateLikeCount(videoId, -1);
        return new ResponseEntity(HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id or like id"));
  }

  @GetMapping("/{id:.+}/likes")
  public CursorResponse getLikedMemberList(@PathVariable Long id,
                                           @RequestParam(defaultValue = "100") int count,
                                           @RequestParam(required = false) String cursor) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<VideoLike> list = videoLikeRepository.findByVideoIdAndCreatedAtBeforeAndVideoDeletedAtIsNull(id, startCursor, pageable);
    List<MemberInfo> members = Lists.newArrayList();
    list.stream().forEach(view -> members.add(memberService.getMemberInfo(view.getCreatedBy())));

    String nextCursor = null;
    if (members.size() > 0) {
      nextCursor = String.valueOf(list.getContent().get(list.getContent().size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/videos/" + id + "/likes", members)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  /**
   * Comment Likes
   */
  @Transactional
  @PostMapping("/{videoId:.+}/comments/{commentId:.+}/likes")
  public ResponseEntity<CommentLikeInfo> addCommentLike(@PathVariable Long videoId,
                                                                  @PathVariable Long commentId) {
    Member member = memberService.currentMember();
    if (member == null) {
      throw new MemberNotFoundException("Login required");
    }

    return commentRepository.findByIdAndVideoId(commentId, videoId)
        .map(comment -> {
          if (commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId
              ()).isPresent()) {
            throw new BadRequestException("duplicated_video_comment_like", "Already video comment liked");
          }


          commentRepository.updateLikeCount(comment.getId(), 1);
          comment.setLikeCount(comment.getLikeCount() + 1);
          CommentLike commentLikeLike = commentLikeRepository.save(new CommentLike(comment));
          return new ResponseEntity<>(new CommentLikeInfo(commentLikeLike), HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video or " +
            "comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeCommentLike(@PathVariable Long videoId,
                                                 @PathVariable Long commentId,
                                                 @PathVariable Long likeId){
    Member me = memberService.currentMember();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    Comment comment = commentRepository.findByIdAndVideoId(commentId, videoId)
        .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video id or comment " +
            "id"));

    return commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment
        .getId(), me.getId())
        .map(liked -> {
          commentLikeRepository.delete(liked);
          commentRepository.updateLikeCount(liked.getComment().getId(), -1);

          return new ResponseEntity(HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("video_comment_like_not_found", "invalid video " +
            "comment like id"));
  }

  /**
   * Watches
   */
  @Transactional
  @PostMapping(value = "/{id:.+}/watches")
  public ResponseEntity<VideoInfo> joinWatch(@PathVariable Long id) {
    VideoInfo video;
    Member me = memberService.currentMember();
    if (me == null) { // Guest
      video = videoService.setWatcherWithGuest(id, memberService.getGuestUserName());
    } else {
      video = videoService.setWatcher(id, me);
    }

    return new ResponseEntity<>(video, HttpStatus.OK);
  }

  @Transactional
  @PatchMapping(value = "/{id:.+}/watches")
  public ResponseEntity<VideoInfo> keepWatch(@PathVariable Long id) {
    VideoInfo video;
    Member me = memberService.currentMember();
    if (me == null) { // Guest
      video = videoService.setWatcherWithGuest(id, memberService.getGuestUserName());
    } else {
      video = videoService.setWatcher(id, me);
    }

    return new ResponseEntity<>(video, HttpStatus.OK);
  }

  @Transactional
  @DeleteMapping("/{id:.+}/watches")
  public ResponseEntity<?> leaveWatch(@PathVariable Long id) {
    videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));

    Member me = memberService.currentMember();

    if (me == null) { // Guest
      videoWatchRepository.findByVideoIdAndUsername(id, memberService.getGuestUserName())
        .ifPresent(videoWatchRepository::delete);
    } else {
      videoWatchRepository.findByVideoIdAndCreatedById(id, me.getId())
        .ifPresent(videoWatchRepository::delete);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id:.+}/watches")
  public CursorResponse getWatcherList(@PathVariable Long id,
                                       @RequestParam(defaultValue = "100") int count,
                                       @RequestParam(required = false) String cursor) {
    Long startCursor = StringUtils.isBlank(cursor) ? 0 : Long.parseLong(cursor);  // "createdBy" is used for cursor
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "createdBy"));
    long duration = new Date().getTime() - watchDuration;
    Slice<VideoWatch> list = videoWatchRepository.findByVideoIdAndIsGuestIsFalseAndModifiedAtAfterAndCreatedByIdAfter(id, new Date(duration), startCursor, pageable);
    List<MemberInfo> members = Lists.newArrayList();
    list.stream().forEach(watch -> members.add(memberService.getMemberInfo(watch.getCreatedBy())));

    String nextCursor = null;
    if (members.size() > 0) {
      nextCursor = String.valueOf(members.get(members.size() - 1).getId());
    }

    return new CursorResponse.Builder<>("/api/1/videos/" + id + "/watches", members)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  /**
   * Using owner of the Video
   */
  @GetMapping("/{id:.+}/sales")
  public ResponseEntity<RevenueOverview> getRevenueOverview(@PathVariable Long id) {
    Member member = memberService.currentMember();
    RevenueOverview overview = revenueService.getOverview(id, member);

    return new ResponseEntity<>(overview, HttpStatus.OK);
  }

  /**
   * Using owner of the Video
   */
  @GetMapping("/{id:.+}/revenues")
  public CursorResponse getRevenues(@PathVariable Long id,
                                     @RequestParam(defaultValue = "20") int count,
                                     @RequestParam(required = false) String cursor) {

    Long memberId = memberService.currentMemberId();
    Video video = videoRepository.findByIdAndMemberId(id, memberId)
       .orElseThrow(() -> new NotFoundException("video_not_fount", "invalid video id or member id"));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    Slice<Revenue>  list = null;

    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      list = revenueRepository.findByVideoAndCreatedAtBefore(video, createdAt, pageable);
    } else {
      list = revenueRepository.findByVideo(video, pageable);
    }

    List<RevenueInfo> revenues = Lists.newArrayList();

    list.forEach(r -> {
      revenues.add(new RevenueInfo(r));
    });

    String nextCursor = null;
    if (revenues.size() > 0) {
      nextCursor = String.valueOf(revenues.get(revenues.size() - 1).getCreatedAt());
    }

    return new CursorResponse.Builder<>("/api/1/videos/" + id + "/revenues", revenues)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  /**
   * Report
   */
  @Transactional
  @PostMapping(value = "/{id:.+}/report")
  public ResponseEntity<VideoInfo> reportVideo(@PathVariable Long id,
                                               @Valid @RequestBody VideoReportRequest request,
                                               BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Member me = memberService.currentMember();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));

    Optional<VideoReport> optional = videoReportRepository.findByVideoIdAndCreatedById(id, me.getId());
    if (optional.isPresent()) {
      throw new BadRequestException("already_reported");
    } else {
      videoReportRepository.save(new VideoReport(video, me, request.getReason()));
    }

    return new ResponseEntity<>(videoService.generateVideoInfo(video), HttpStatus.OK);
  }

  /**
   * Add Heart
   */
  @Transactional
  @PostMapping(value = "/{id:.+}/hearts")
  public ResponseEntity<VideoInfo> heartVideo(@PathVariable Long id,
                                               @Valid @RequestBody(required = false) VideoHeartRequest request,
                                               BindingResult bindingResult) {
    // Guest can add heart_count
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
    int count = 1;
    if (request != null && request.getCount() != null) {
      count = request.getCount();
    }

    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));

    videoRepository.updateHeartCount(id, count);
    video.setHeartCount(video.getHeartCount() + count);

    return new ResponseEntity<>(videoService.generateVideoInfo(video), HttpStatus.OK);
  }

  /**
   * Views
   */
  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<VideoInfo> addView(@PathVariable Long id) {
    Member me = memberService.currentMember();

    return videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(v -> {
        videoRepository.updateViewCount(v.getId(), 1);
        v.setViewCount(v.getViewCount() + 1);

        if (me != null) {   // Guest can add view_count, but can not be inserted into viewer list
          Optional<VideoView> optional = videoViewRepository.findByVideoIdAndCreatedById(id, me.getId());
          if (optional.isPresent()) {
            VideoView view = optional.get();
            view.setModifiedAt(new Date());
            videoViewRepository.save(view);
          } else {
            videoViewRepository.save(new VideoView(v, me));
          }
        }

        return new ResponseEntity<>(videoService.generateVideoInfo(v), HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));
  }

  @GetMapping("/{id:.+}/views")
  public CursorResponse getViewerList(@PathVariable Long id,
                                       @RequestParam(defaultValue = "100") int count,
                                       @RequestParam(required = false) String cursor) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "modifiedAt"));
    Slice<VideoView> list = videoViewRepository.findByVideoIdAndModifiedAtBefore(id, startCursor, pageable);
    List<MemberInfo> members = Lists.newArrayList();
    list.stream().forEach(view -> members.add(memberService.getMemberInfo(view.getCreatedBy())));

    String nextCursor = null;
    if (members.size() > 0) {
      nextCursor = String.valueOf(list.getContent().get(list.getContent().size() - 1).getModifiedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/videos/" + id + "/views", members)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  @Data
  @NoArgsConstructor
  public static class VideoInfo {
    private Long id;
    private String videoKey;
    private String type;
    private String state;
    private String visibility;
    private String title;
    private String content;
    private String url;
    private String thumbnailPath;
    private String thumbnailUrl;
    private String chatRoomId;
    private Integer duration;
    private String data;
    private Integer watchCount;
    private Integer totalWatchCount;
    private Integer viewCount;
    private Integer heartCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer orderCount;
    private Integer relatedGoodsCount;
    private String relatedGoodsThumbnailUrl;
    private Long likeId;
    private MemberInfo owner;
    private Boolean blocked;
    private Date createdAt;

    public VideoInfo(Video video, MemberInfo owner, Long likeId, Boolean blocked) {
      BeanUtils.copyProperties(video, this);
      this.owner = owner;
      this.likeId = likeId;
      this.blocked = blocked;
      if (this.relatedGoodsCount == null) { this.relatedGoodsCount = 0;}  // FIXME: check policy
      if (this.relatedGoodsThumbnailUrl == null) { this.relatedGoodsThumbnailUrl = "";} // FIXME: check policy
    }
  }

  @Data
  private static class CreateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;

    private Long parentId;

    private List<MentionTag> mentionTags;
  }

  @Data
  private static class UpdateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;
  }

  @Data
  public static class VideoLikeInfo {
    private Long id;
    private Long createdBy;
    private Date createdAt;
    private VideoInfo video;

    VideoLikeInfo(VideoLike videoLike, VideoInfo video) {
      BeanUtils.copyProperties(videoLike, this);
      this.video = video;
    }
  }

  @Data
  public static class CommentLikeInfo {
    private Long id;
    private MemberInfo createdBy;
    private Date createdAt;
    private CommentInfo comment;

    public CommentLikeInfo(CommentLike commentLike) {
      BeanUtils.copyProperties(commentLike, this);
      comment = new CommentInfo(commentLike.getComment());
    }
  }

  @Data
  private static class VideoReportRequest {
    @NotNull
    @Size(max = 80)
    private String reason;
  }

  @Data
  private static class VideoHeartRequest {
    private Integer count;
  }
}