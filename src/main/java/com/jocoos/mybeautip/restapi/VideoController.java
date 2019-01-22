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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.AccessDeniedException;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentInfo;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.comment.CommentService;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.member.revenue.Revenue;
import com.jocoos.mybeautip.member.revenue.RevenueInfo;
import com.jocoos.mybeautip.member.revenue.RevenueOverview;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.member.revenue.RevenueService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
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
  private final MessageService messageService;
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
  private final NotificationService notificationService;
  private final RevenueRepository revenueRepository;
  private final GoodsRepository goodsRepository;
  private final ObjectMapper objectMapper;

  private static final String VIDEO_NOT_FOUND = "video.not_found";
  private static final String VIDEO_ALREADY_REPORTED = "video.already_reported";
  private static final String COMMENT_NOT_FOUND = "comment.not_found";
  private static final String ALREADY_LIKED = "like.already_liked";
  private static final String COMMENT_WRITE_NOT_ALLOWED = "comment.write_not_allowed";
  
  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;
  
  public VideoController(MemberService memberService,
                         VideoService videoService,
                         MessageService messageService,
                         GoodsService goodsService,
                         VideoGoodsRepository videoGoodsRepository,
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
                         NotificationService notificationService,
                         RevenueRepository revenueRepository,
                         GoodsRepository goodsRepository,
                         ObjectMapper objectMapper) {
    this.memberService = memberService;
    this.videoService = videoService;
    this.messageService = messageService;
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
    this.notificationService = notificationService;
    this.revenueRepository = revenueRepository;
    this.goodsRepository = goodsRepository;
    this.objectMapper = objectMapper;
  }
  
  @Transactional
  @PostMapping
  public VideoInfo createVideo(@Valid @RequestBody CreateVideoRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.info("callback createVideo: {}", request.toString());

    Video video = new Video(memberService.currentMember());
    BeanUtils.copyProperties(request, video);
    
    if (StringUtils.isNotEmpty(video.getContent())) {
      List<String> tags = tagService.getHashTagsAndIncreaseRefCount(video.getContent());
      if (tags != null && tags.size() > 0) {
        try {
          video.setTagInfo(objectMapper.writeValueAsString(tags));
        } catch (JsonProcessingException e) {
          log.warn("tag parsing failed, tags: ", tags.toString());
        }
        
        // Log TagHistory
        tagService.logHistory(tags, TagService.TagCategory.VIDEO, memberService.currentMember());
      }
    }
    Video createdVideo = videoRepository.save(video); // do not notify
    
    // Set related goods info
    if (StringUtils.isNotEmpty(request.getData())) {
      String[] userData = StringUtils.deleteWhitespace(request.getData()).split(",");
      List<VideoGoods> videoGoods = new ArrayList<>();
      for (String goods : userData) {
        if (goods.length() != 10) { // invalid goodsNo
          continue;
        }
        goodsRepository.findByGoodsNo(goods).map(g -> {
          videoGoods.add(new VideoGoods(createdVideo, g, createdVideo.getMember()));
          return Optional.empty();
        });
      }
      
      if (videoGoods.size() > 0) {
        videoGoodsRepository.saveAll(videoGoods);
        
        // Set related goods count & one thumbnail image
        String url = videoGoods.get(0).getGoods().getListImageData().toString();
        createdVideo.setRelatedGoodsThumbnailUrl(url);
        createdVideo.setRelatedGoodsCount(videoGoods.size());
        videoRepository.save(createdVideo);
      }
    }
    
    return videoService.generateVideoInfo(createdVideo);
  }

  @GetMapping("{id}")
  public VideoInfo getVideos(@PathVariable Long id,
                             @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(videoService::generateVideoInfo)
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
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
  
  @Transactional
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
  public CursorResponse getComments(@PathVariable Long id,
                                    @RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) Long cursor,
                                    @RequestParam(required = false) String direction,
                                    @RequestParam(name = "parent_id", required = false) Long parentId) {
    PageRequest page;
    if ("next".equals(direction)) {
      page = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    } else {
      page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id")); // default
    }
    
    Slice<Comment> comments;
    if (parentId != null) {
      comments = videoService.findCommentsByParentId(parentId, cursor, page, direction);
    } else {
      comments = videoService.findCommentsByVideoId(id, cursor, page, direction);
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
      if ("next".equals(direction)) {
        nextCursor = String.valueOf(result.get(result.size() - 1).getId() + 1);
      } else {
        nextCursor = String.valueOf(result.get(result.size() - 1).getId() - 1);
      }
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
                                   BindingResult bindingResult,
                                   @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
  
    Member member = memberService.currentMember();
    if (!memberService.hasCommentPostPermission(member)) {
      throw new BadRequestException("invalid_permission", messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
    }
    
    videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

    if (request.getParentId() != null) {
      commentRepository.findById(request.getParentId())
        .map(parent -> {
          commentRepository.updateCommentCount(parent.getId(), 1);
          return Optional.empty();
        })
        .orElseThrow(() -> new NotFoundException("comment_not_found", messageService.getMessage(COMMENT_NOT_FOUND, lang)));
    }

    Comment comment = new Comment();
    comment.setVideoId(id);
    BeanUtils.copyProperties(request, comment);
    
    tagService.parseHashTagsAndToucheRefCount(comment.getComment(), TagService.TagCategory.COMMENT, memberService.currentMember());
    videoRepository.updateCommentCount(id, 1);

    commentService.save(comment);

    List<MentionTag> mentionTags = request.getMentionTags();
    if (mentionTags != null && mentionTags.size() > 0) {
      mentionService.updateVideoCommentWithMention(comment, mentionTags);
    } else {
      notificationService.notifyAddComment(comment);
    }

    return new ResponseEntity<>(
      new CommentInfo(comment),
      HttpStatus.OK
    );
  }
  
  @Transactional
  @PatchMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity updateComment(@PathVariable Long videoId,
                                           @PathVariable Long id,
                                           @RequestBody VideoController.UpdateCommentRequest request,
                                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang,
                                           BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
  
    Member member = memberService.currentMember();
    if (!memberService.hasCommentPostPermission(member)) {
      throw new BadRequestException("invalid_permission", messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
    }

    return commentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, member.getId())
      .map(comment -> {
        comment.setComment(request.getComment());
        tagService.parseHashTagsAndToucheRefCount(comment.getComment(), TagService.TagCategory.COMMENT, memberService.currentMember());
        return new ResponseEntity<>(
          new CommentInfo(commentRepository.save(comment)),
          HttpStatus.OK
        );
      })
      .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid video key id or comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeComment(@PathVariable Long videoId,
                                         @PathVariable Long id) {
    return commentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberService.currentMemberId())
      .map(comment -> {
        videoService.deleteComment(comment);
        return new ResponseEntity<>(HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid video key or comment id"));
  }

  /**
   * Likes
   */
  @Transactional
  @PostMapping("/{videoId:.+}/likes")
  public ResponseEntity<VideoLikeInfo> addVideoLike(@PathVariable Long videoId,
                                                    @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    
    return videoRepository.findByIdAndDeletedAtIsNull(videoId)
      .map(video -> {
        if (videoLikeRepository.findByVideoIdAndCreatedById(videoId, memberId).isPresent()) {
          throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
        }

        videoRepository.updateLikeCount(videoId, 1);
        video.setLikeCount(video.getLikeCount() + 1);
        VideoLike videoLike = videoLikeRepository.save(new VideoLike(video));
        VideoLikeInfo info = new VideoLikeInfo(videoLike, videoService.generateVideoInfo(video));
        return new ResponseEntity<>(info, HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeVideoLike(@PathVariable Long videoId,
                                           @PathVariable Long likeId,
                                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    
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
                                                        @PathVariable Long commentId,
                                                        @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberService.currentMember();
    
    return commentRepository.findByIdAndVideoId(commentId, videoId)
        .map(comment -> {
          if (commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId
              ()).isPresent()) {
            throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
          }


          commentRepository.updateLikeCount(comment.getId(), 1);
          comment.setLikeCount(comment.getLikeCount() + 1);
          CommentLike commentLikeLike = commentLikeRepository.save(new CommentLike(comment));
          return new ResponseEntity<>(new CommentLikeInfo(commentLikeLike), HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid video or comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeCommentLike(@PathVariable Long videoId,
                                                 @PathVariable Long commentId,
                                                 @PathVariable Long likeId){
    Member me = memberService.currentMember();
    
    Comment comment = commentRepository.findByIdAndVideoId(commentId, videoId)
        .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid video id or comment " +
            "id"));

    return commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment
        .getId(), me.getId())
        .map(liked -> {
          commentLikeRepository.delete(liked);
          commentRepository.updateLikeCount(liked.getComment().getId(), -1);

          return new ResponseEntity(HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("comment_like_not_found", "invalid video " +
            "comment like id"));
  }

  /**
   * Watches
   */
  @Transactional
  @PostMapping(value = "/{id:.+}/watches")
  public ResponseEntity<VideoInfo> joinWatch(@PathVariable Long id,
                                             @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member me = memberService.currentMember();
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
        .map(v -> {
          if (me == null) { // Guest
            videoService.setWatcherWithGuest(v, memberService.getGuestUserName());
          } else {
            videoService.setWatcher(v, me);
          }
          return videoService.addView(v, me);
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
    
    return new ResponseEntity<>(videoService.generateVideoInfo(video), HttpStatus.OK);
  }

  @Transactional
  @PatchMapping(value = "/{id:.+}/watches")
  public ResponseEntity<VideoInfo> keepWatch(@PathVariable Long id,
                                             @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member me = memberService.currentMember();
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
        .map(v -> {
          if (me == null) { // Guest
            videoService.setWatcherWithGuest(v, memberService.getGuestUserName());
          } else {
            videoService.setWatcher(v, me);
          }
          return v;
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
  
    return new ResponseEntity<>(videoService.generateVideoInfo(video), HttpStatus.OK);
  }

  @Transactional
  @DeleteMapping("/{id:.+}/watches")
  public ResponseEntity<?> leaveWatch(@PathVariable Long id,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

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

    int guestCount = videoWatchRepository.countByVideoIdAndIsGuestIsTrueAndModifiedAtAfter(id, new Date(duration));

    String nextCursor = null;
    if (members.size() > 0) {
      nextCursor = String.valueOf(members.get(members.size() - 1).getId());
    }

    return new CursorResponse.Builder<>("/api/1/videos/" + id + "/watches", members)
      .withCount(count)
      .withGuestCount(guestCount)
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
                                    @RequestParam(required = false) String cursor,
                                    @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {

    Long memberId = memberService.currentMemberId();
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
       .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

    if (!memberId.equals(video.getMember().getId())) {
      throw new AccessDeniedException("Invalid member id");
    }

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    Slice<Revenue>  list;

    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      list = revenueRepository.findByVideoAndCreatedAtBefore(video, createdAt, pageable);
    } else {
      list = revenueRepository.findByVideo(video, pageable);
    }

    List<RevenueInfo> revenues = Lists.newArrayList();

    list.forEach(r -> revenues.add(new RevenueInfo(r)));

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
                                               BindingResult bindingResult,
                                               @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
    
    Member me = memberService.currentMember();
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

    Optional<VideoReport> optional = videoReportRepository.findByVideoIdAndCreatedById(id, me.getId());
    if (optional.isPresent()) {
      throw new BadRequestException("already_reported", messageService.getMessage(VIDEO_ALREADY_REPORTED, lang));
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
                                              BindingResult bindingResult,
                                              @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    // Guest can add heart_count
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
    int count = 1;
    if (request != null && request.getCount() != null) {
      count = request.getCount();
    }

    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

    videoRepository.updateHeartCount(id, count);
    video.setHeartCount(video.getHeartCount() + count);

    return new ResponseEntity<>(videoService.generateVideoInfo(video), HttpStatus.OK);
  }

  /**
   * Views
   */
  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<VideoInfo> addView(@PathVariable Long id,
                                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
        .map(v -> videoService.addView(v, memberService.currentMember()))
        .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
    
    return new ResponseEntity<>(videoService.generateVideoInfo(video), HttpStatus.OK);
  }

  @GetMapping("/{id:.+}/views")
  public CursorResponse getViewerList(@PathVariable Long id,
                                      @RequestParam(defaultValue = "100") int count,
                                      @RequestParam(required = false) String cursor,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "modifiedAt"));

    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
       .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

    Slice<VideoView> list = videoViewRepository.findByVideoIdAndAndCreatedByIsNotNullAndModifiedAtBefore(id, startCursor, pageable);
    List<MemberInfo> members = Lists.newArrayList();
    list.stream().forEach(view -> members.add(memberService.getMemberInfo(view.getCreatedBy())));

    String nextCursor = null;

    if (members.size() > 0) {
      nextCursor = String.valueOf(list.getContent().get(list.getContent().size() - 1).getModifiedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/videos/" + id + "/views", members)
      .withCount(count)
      .withGuestCount(videoViewRepository.countByVideoIdAndCreatedByIsNull(id))
      .withCursor(nextCursor).toBuild();
  }

  @Data
  @NoArgsConstructor
  public static class VideoInfo {
    private Long id;
    private String videoKey;
    private String type;
    private String state;
    private Boolean locked;
    private Boolean muted;
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
  public static class CreateVideoRequest {
    @NotNull
    String type = "BROADCASTED";
    String visibility = "PUBLIC";
    String title ="";
    String content = "";
    String chatRoomId ="";
    String data = "";
    Boolean muted = false;
    Boolean locked = false;
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