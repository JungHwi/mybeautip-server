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
  private final VideoCommentRepository videoCommentRepository;
  private final VideoCommentLikeRepository videoCommentLikeRepository;
  private final VideoWatchRepository videoWatchRepository;
  private final VideoReportRepository videoReportRepository;
  private final VideoViewRepository videoViewRepository;

  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;

  public VideoController(MemberService memberService,
                         VideoService videoService,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsService goodsService,
                         VideoRepository videoRepository,
                         VideoCommentRepository videoCommentRepository,
                         VideoLikeRepository videoLikeRepository,
                         VideoCommentLikeRepository videoCommentLikeRepository,
                         VideoWatchRepository videoWatchRepository,
                         VideoReportRepository videoReportRepository,
                         VideoViewRepository videoViewRepository) {
    this.memberService = memberService;
    this.videoService = videoService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsService = goodsService;
    this.videoRepository = videoRepository;
    this.videoCommentRepository = videoCommentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.videoCommentLikeRepository = videoCommentLikeRepository;
    this.videoWatchRepository = videoWatchRepository;
    this.videoReportRepository = videoReportRepository;
    this.videoViewRepository = videoViewRepository;
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
    List<VideoGoods> list = videoGoodsRepository.findAllByVideoId(id);

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (VideoGoods video : list) {
      relatedGoods.add(goodsService.generateGoodsInfo(video.getGoods()));
    }
    return relatedGoods;
  }

  @GetMapping("/{id}/comments")
  public CursorResponse getVideoComments(@PathVariable Long id,
                                         @RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) String cursor,
                                         @RequestParam(required = false) Long parentId) {
    PageRequest page = PageRequest.of(0, count);
    Slice<VideoComment> comments;
    Long me = memberService.currentMemberId();

    if (parentId != null) {
      comments = videoService.findCommentsByParentId(parentId, cursor, page);
    } else {
      comments = videoService.findCommentsByVideoId(id, cursor, page);
    }

    List<VideoController.VideoCommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
      VideoCommentInfo commentInfo = new VideoCommentInfo(comment, createMemberInfo(comment
          .getCreatedBy()));
      if (me != null) {
        videoCommentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), me)
          .ifPresent(liked -> commentInfo.setLikeId(liked.getId()));
      }
      result.add(commentInfo);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    int totalCount = videoRepository.findById(id).map(Video::getLikeCount).orElse(0);

    return new CursorResponse
      .Builder<>("/api/1/videos/" + id + "/comments", result)
      .withCount(count)
      .withCursor(nextCursor)
      .withTotalCount(totalCount).toBuild();
  }

  @Transactional
  @PostMapping("/{id:.+}/comments")
  public ResponseEntity addVideoComment(@PathVariable Long id,
                                        @RequestBody VideoController.CreateCommentRequest request,
                                        BindingResult bindingResult) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Member me = memberService.currentMember();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));

    if (request.getParentId() != null) {
      videoCommentRepository.findById(request.getParentId())
        .map(parent -> {
          videoCommentRepository.updateCommentCount(parent.getId(), 1);
          return Optional.empty();
        })
        .orElseThrow(() -> new NotFoundException("comment_id_not_found", "invalid comment parent id"));
    }

    VideoComment videoComment = new VideoComment(id);
    BeanUtils.copyProperties(request, videoComment);
    videoRepository.updateCommentCount(id, 1);

    return new ResponseEntity<>(
      new VideoController.VideoCommentInfo(videoCommentRepository.save(videoComment)),
      HttpStatus.OK
    );
  }

  @PatchMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity updateVideoComment(@PathVariable Long videoId,
                                           @PathVariable Long id,
                                           @RequestBody VideoController.UpdateCommentRequest request,
                                           BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return videoCommentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberId)
      .map(comment -> {
        comment.setComment(request.getComment());
        return new ResponseEntity<>(
          new VideoController.VideoCommentInfo(videoCommentRepository.save(comment)),
          HttpStatus.OK
        );
      })
      .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video key id or comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeVideoComment(@PathVariable Long videoId,
                                              @PathVariable Long id) {
    videoRepository.updateCommentCount(videoId, -1);

    Long memberId = memberService.currentMemberId();
    return videoCommentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberId)
      .map(comment -> {
        if (comment.getParentId() != null) {
          videoCommentRepository.updateCommentCount(comment.getParentId(), -1);
        }
        List<VideoCommentLike> commentLikes = videoCommentLikeRepository.findAllByCommentId(
            comment.getId());
        videoCommentLikeRepository.deleteAll(commentLikes);
        videoCommentRepository.delete(comment);
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
    Slice<VideoLike> list = videoLikeRepository.findByVideoIdAndCreatedAtBefore(id, startCursor, pageable);
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
  public ResponseEntity<VideoCommentLikeInfo> addVideoCommentLike(@PathVariable Long videoId,
                                                                  @PathVariable Long commentId) {
    Member member = memberService.currentMember();
    if (member == null) {
      throw new MemberNotFoundException("Login required");
    }

    return videoCommentRepository.findByIdAndVideoId(commentId, videoId)
        .map(comment -> {
          if (videoCommentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId
              ()).isPresent()) {
            throw new BadRequestException("duplicated_video_comment_like", "Already video comment liked");
          }


          videoCommentRepository.updateLikeCount(comment.getId(), 1);
          comment.setLikeCount(comment.getLikeCount() + 1);
          VideoCommentLike commentLikeLike = videoCommentLikeRepository.save(new VideoCommentLike(comment));
          return new ResponseEntity<>(new VideoCommentLikeInfo(commentLikeLike), HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video or " +
            "comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeVideoCommentLike(@PathVariable Long videoId,
                                                 @PathVariable Long commentId,
                                                 @PathVariable Long likeId){
    Member me = memberService.currentMember();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    VideoComment videoComment = videoCommentRepository.findByIdAndVideoId(commentId, videoId)
        .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video id or comment " +
            "id"));

    return videoCommentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, videoComment
        .getId(), me.getId())
        .map(liked -> {
          videoCommentLikeRepository.delete(liked);
          videoCommentRepository.updateLikeCount(liked.getComment().getId(), -1);

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
    private Date createdAt;

    public VideoInfo(Video video, MemberInfo owner, Long likeId) {
      BeanUtils.copyProperties(video, this);
      this.owner = owner;
      this.likeId = likeId;
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
  }

  @Data
  private static class UpdateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;
  }

  @Data
  static class VideoCommentInfo {
    private Long id;
    private Long videoId;
    private String comment;
    private Long parentId;
    private int commentCount;
    private MemberInfo createdBy;
    private Date createdAt;
    private String commentRef;
    private Long likeId;
    private Integer likeCount;

    VideoCommentInfo(VideoComment comment) {
      BeanUtils.copyProperties(comment, this);
      setCommentRef(comment);
    }

    VideoCommentInfo(VideoComment comment, MemberInfo createdBy) {
      this(comment);
      this.createdBy = createdBy;
    }

    private void setCommentRef(VideoComment comment) {
      if (comment != null && comment.getCommentCount() > 0) {
        this.commentRef = String.format("/api/1/videos/%d/comments?parentId=%d", comment.getVideoId(), comment.getId());
      }
    }
  }

  private MemberInfo createMemberInfo(Member member) {
    return new MemberInfo(member, memberService.getFollowingId(member));
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
  public static class VideoCommentLikeInfo {
    private Long id;
    private MemberInfo createdBy;
    private Date createdAt;
    private VideoCommentInfo comment;

    public VideoCommentLikeInfo(VideoCommentLike commentLike) {
      BeanUtils.copyProperties(commentLike, this);
      comment = new VideoCommentInfo(commentLike.getComment());
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