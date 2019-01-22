package com.jocoos.mybeautip.video;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.CallbackController;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.view.VideoView;
import com.jocoos.mybeautip.video.view.VideoViewRepository;
import com.jocoos.mybeautip.video.watches.VideoWatch;
import com.jocoos.mybeautip.video.watches.VideoWatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VideoService {

  private final MemberService memberService;
  private final MessageService messageService;
  private final TagService tagService;
  private final FeedService feedService;
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final VideoWatchRepository videoWatchRepository;
  private final BlockRepository blockRepository;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final VideoViewRepository videoViewRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final ObjectMapper objectMapper;

  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;

  private static final String VIDEO_NOT_FOUND = "video.not_found";
  
  public VideoService(MemberService memberService,
                      MessageService messageService,
                      TagService tagService,
                      FeedService feedService,
                      VideoRepository videoRepository,
                      CommentRepository commentRepository,
                      VideoLikeRepository videoLikeRepository,
                      VideoWatchRepository videoWatchRepository,
                      BlockRepository blockRepository,
                      MemberRepository memberRepository,
                      GoodsRepository goodsRepository,
                      VideoGoodsRepository videoGoodsRepository,
                      VideoViewRepository videoViewRepository,
                      CommentLikeRepository commentLikeRepository,
                      ObjectMapper objectMapper) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.tagService = tagService;
    this.feedService = feedService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.videoWatchRepository = videoWatchRepository;
    this.blockRepository = blockRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.videoViewRepository = videoViewRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.objectMapper = objectMapper;
  }

  public Slice<Video> findVideosWithKeyword(String keyword, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));

    return videoRepository.searchVideos(keyword, startCursor, page);
  }


  public Slice<Video> findVideos(String type, String state, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));

    switch (getRequestFilter(type, state)) {
      case "all":
        return videoRepository.getAnyoneAllVideos(startCursor, PageRequest.of(0, count));
      case "live":
        return videoRepository.getAnyoneLiveVideos(startCursor, PageRequest.of(0, count));
      case "vod":
        return videoRepository.getAnyoneVodVideos(startCursor, PageRequest.of(0, count));
      case "motd":
        return videoRepository.getAnyoneMotdVideos(startCursor, PageRequest.of(0, count));
      case "vod+motd":
        return videoRepository.getAnyoneVodAndMotdVideos(startCursor, PageRequest.of(0, count));
      case "live+vod":
        return videoRepository.getAnyoneLiveAndVodVideos(startCursor, PageRequest.of(0, count));
      default:
        return null;
    }
  }

  public Slice<Video> findMyVideos(Member me, String type, String state, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));

    switch (getRequestFilter(type, state)) {
      case "all":
        return videoRepository.getMyAllVideos(me, startCursor, PageRequest.of(0, count));
      case "live":
        return videoRepository.getMyLiveVideos(me, startCursor, PageRequest.of(0, count));
      case "vod":
        return videoRepository.getMyVodVideos(me, startCursor, PageRequest.of(0, count));
      case "motd":
        return videoRepository.getMyMotdVideos(me, startCursor, PageRequest.of(0, count));
      case "vod+motd":
        return videoRepository.getMyVodAndMotdVideos(me, startCursor, PageRequest.of(0, count));
      case "live+vod":
        return videoRepository.getMyLiveAndVodVideos(me, startCursor, PageRequest.of(0, count));
      default:
        return null;
    }
  }

  public Slice<Video> findMemberVideos(Member member, String type, String state, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));

    switch (getRequestFilter(type, state)) {
      case "all":
        return videoRepository.getUserAllVideos(member, startCursor, PageRequest.of(0, count));
      case "live":
        return videoRepository.getUserLiveVideos(member, startCursor, PageRequest.of(0, count));
      case "vod":
        return videoRepository.getUserVodVideos(member, startCursor, PageRequest.of(0, count));
      case "motd":
        return videoRepository.getUserMotdVideos(member, startCursor, PageRequest.of(0, count));
      case "vod+motd":
        return videoRepository.getUserVodAndMotdVideos(member, startCursor, PageRequest.of(0, count));
      case "live+vod":
        return videoRepository.getUserLiveAndVodVideos(member, startCursor, PageRequest.of(0, count));
      default:
        return null;
    }
  }

  private String getRequestFilter(String type, String state) {
    if (type == null && state == null) {
      return "all";
    }

    if (type == null && "live".equalsIgnoreCase(state)) {
      return "live";
    }

    if (type == null && "vod".equalsIgnoreCase(state)) {
      return "vod+motd";
    }

    if ("broadcasted".equalsIgnoreCase(type) && state == null) {
      return "live+vod";
    }

    if ("broadcasted".equalsIgnoreCase(type) && "live".equalsIgnoreCase(state)) {
      return "live";
    }

    if ("broadcasted".equalsIgnoreCase(type) && "vod".equalsIgnoreCase(state)) {
      return "vod";
    }

    if ("uploaded".equalsIgnoreCase(type) && state == null) {
      return "motd";
    }

    if ("uploaded".equalsIgnoreCase(type) && "live".equalsIgnoreCase(state)) {
      return "invalid";
    }

    if ("uploaded".equalsIgnoreCase(type) && "vod".equalsIgnoreCase(state)) {
      return "motd";
    }

    return "all";
  }

  public Slice<Comment> findCommentsByVideoId(Long id, Long cursor, Pageable pageable, String direction) {
    Slice<Comment> comments;
    if (cursor != null) {
      if ("next".equals(direction)) {
        comments = commentRepository.findByVideoIdAndIdGreaterThanEqualAndParentIdIsNull(id, cursor, pageable);
      } else {
        comments = commentRepository.findByVideoIdAndIdLessThanEqualAndParentIdIsNull(id, cursor, pageable);
      }
    } else {
      comments = commentRepository.findByVideoIdAndParentIdIsNull(id, pageable);
    }
    return comments;
  }

  public Slice<Comment> findCommentsByParentId(Long parentId, Long cursor, Pageable pageable, String direction) {
    Slice<Comment> comments;
    if (cursor != null) {
      if ("next".equals(direction)) {
        comments = commentRepository.findByParentIdAndIdGreaterThanEqual(parentId, cursor, pageable);
      } else {
        comments = commentRepository.findByParentIdAndIdLessThanEqual(parentId, cursor, pageable);
      }
    } else {
      comments = commentRepository.findByParentId(parentId, pageable);
    }
    return comments;
  }

  public VideoController.VideoInfo generateVideoInfo(Video video) {
    Long likeId = null;
    boolean blocked = false;
    
    Long me = memberService.currentMemberId();
    // Set likeID
    if (me != null) {
      Optional<VideoLike> optional = videoLikeRepository.findByVideoIdAndCreatedById(video.getId(), me);
      likeId = optional.map(VideoLike::getId).orElse(null);
      
      blocked = blockRepository.findByMeAndMemberYouId(video.getMember().getId(), me).isPresent();
    }
    // Set Watch count
    if ("live".equalsIgnoreCase(video.getState())) {
      long duration = new Date().getTime() - watchDuration;
      video.setWatchCount(videoWatchRepository.countByVideoIdAndModifiedAtAfter(video.getId(), new Date(duration)));
    }
    return new VideoController.VideoInfo(video, memberService.getMemberInfo(video.getMember()), likeId, blocked);
  }

  @Transactional
  public Video setWatcher(Video video, Member me) {
    if ("live".equalsIgnoreCase(video.getState())) {
      videoWatchRepository.findByVideoIdAndCreatedById(video.getId(), me.getId())
          .map(watch -> {
            watch.setModifiedAt(new Date());
            videoWatchRepository.save(watch);
            return Optional.empty();
          })
          .orElseGet(() -> {
            videoWatchRepository.save(new VideoWatch(video, me));
            video.setTotalWatchCount(video.getTotalWatchCount() + 1);
            return Optional.empty();
          });
      return videoRepository.saveAndFlush(video);
    }
    return video;
  }

  @Transactional
  public Video setWatcherWithGuest(Video video, String guestUsername) {
    if ("live".equalsIgnoreCase(video.getState())) {
      videoWatchRepository.findByVideoIdAndUsername(video.getId(), guestUsername)
          .map(watch -> {
            watch.setModifiedAt(new Date());
            videoWatchRepository.save(watch);
            return Optional.empty();
          })
          .orElseGet(() -> {
            videoWatchRepository.save(new VideoWatch(video, guestUsername));
            video.setTotalWatchCount(video.getTotalWatchCount() + 1);
            video.setViewCount(video.getViewCount() + 1);
            return Optional.empty();
          });
      return videoRepository.saveAndFlush(video);
    }
    return video;
  }
  
  @Transactional
  public Video startVideo(CallbackController.CallbackStartVideoRequest request, Member member) {
    Video video;
    if ("UPLOADED".equals(request.getType())) {
      video = new Video(member);
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
          tagService.logHistory(tags, TagService.TagCategory.VIDEO, member);
        }
      }
      Video createdVideo = videoRepository.save(video);
    
      // Set related goods info
      if (StringUtils.isNotEmpty(request.getData())) {
        String[] userData = StringUtils.deleteWhitespace(request.getData()).split(",");
        List<VideoGoods> videoGoods = new ArrayList<>();
        for (String goods : userData) {
          if (goods.length() != 10) { // invalid goodsNo
            continue;
          }
          goodsRepository.findByGoodsNo(goods).ifPresent(g -> {
            videoGoods.add(new VideoGoods(createdVideo, g, createdVideo.getMember()));
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
    
      if ("PUBLIC".equals(request.getVisibility())) {
        member.setPublicVideoCount(member.getPublicVideoCount() + 1);
      }
      member.setTotalVideoCount(member.getTotalVideoCount() + 1);
      memberRepository.save(member);
  
      video = videoRepository.save(video);
      if ("PUBLIC".equals(video.getVisibility())) {
        feedService.feedVideo(video);
      }
      return video;
    } else {
      video = videoRepository.findById(Long.parseLong(request.getVideoKey()))
          .orElseGet(() -> {
            log.error("Cannot find videoId: " + request.getVideoKey());
            throw new NotFoundException("video_not_found", "video not found, video_id:" + request.getVideoKey());
          });
      BeanUtils.copyProperties(request, video);
    
      if ("PUBLIC".equals(request.getVisibility())) {
        member.setPublicVideoCount(member.getPublicVideoCount() + 1);
      }
      member.setTotalVideoCount(member.getTotalVideoCount() + 1);
      memberRepository.save(member);
    
      video = videoRepository.save(video);
      if ("PUBLIC".equals(video.getVisibility())) {
        feedService.feedVideo(video);
      }
      return video;
    }
  }
  
  @Transactional
  public Video deleteVideo(long memberId, Long videoId) {
    return videoRepository.findByIdAndDeletedAtIsNull(videoId)
        .map(v -> {
          if (v.getMember().getId() != memberId) {
            throw new BadRequestException("invalid_user_id", "Invalid user_id: " + memberId);
          }
          tagService.decreaseRefCount(v.getTagInfo());
          saveWithDeletedAt(v);
          videoLikeRepository.deleteByVideoId(v.getId());
          Member member = v.getMember();
          if ("PUBLIC".equals(v.getVisibility())) {
            member.setPublicVideoCount(member.getPublicVideoCount() - 1);
          }

          member.setTotalVideoCount(member.getTotalVideoCount() - 1);
          memberRepository.save(member);
          return v;
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, videoId: " + videoId));
  }
  
  @Transactional
  public Video deleteVideo(long memberId, String videoKey) {
    return videoRepository.findByVideoKeyAndDeletedAtIsNull(videoKey)
        .map(v -> {
          if (v.getMember().getId() != memberId) {
            throw new BadRequestException("invalid_user_id", "Invalid user_id: " + memberId);
          }
          tagService.decreaseRefCount(v.getTagInfo());
          saveWithDeletedAt(v);
          videoLikeRepository.deleteByVideoId(v.getId());
          Member member = v.getMember();
          if ("PUBLIC".equals(v.getVisibility())) {
            member.setPublicVideoCount(member.getPublicVideoCount() - 1);
          }
          member.setTotalVideoCount(member.getTotalVideoCount() - 1);
          memberRepository.save(member);
          return v;
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, videoKey: " + videoKey));
  }
  
  // Delete All user's videos when member left
  @Transactional
  public void deleteVideos(Member member) {
    videoRepository.findByMemberAndDeletedAtIsNull(member)
        .forEach(video -> {
          tagService.decreaseRefCount(video.getTagInfo());
          video.setDeletedAt(new Date());
          saveWithDeletedAt(video);
          videoLikeRepository.deleteByVideoId(video.getId());
          feedService.feedDeletedVideo(video.getId());
        });
  }
  
  @Transactional
  public Video addView(Video video, Member me) {
    if (me != null) {
      videoViewRepository.findByVideoIdAndCreatedById(video.getId(), me.getId())
          .map(view -> {
            view.setViewCount(view.getViewCount() + 1);
            videoViewRepository.save(view);
            return Optional.empty();
          })
          .orElseGet(() -> {
            videoViewRepository.save(new VideoView(video, me));
            return Optional.empty();
          });
    } else {  // Guest can add view_count, but can not be inserted into viewer list
      String guestName = memberService.getGuestUserName();
      videoViewRepository.findByVideoIdAndGuestName(video.getId(), guestName)
          .map(view -> {
            view.setViewCount(view.getViewCount() + 1);
            videoViewRepository.save(view);
            return Optional.empty();
          })
          .orElseGet(() -> {
            videoViewRepository.save(new VideoView(video, guestName));
            return Optional.empty();
          });
    }
    
    video.setViewCount(video.getViewCount() + 1);
    return videoRepository.saveAndFlush(video);
  }
  
  @Transactional
  public void deleteComment(Comment comment) {
    videoRepository.updateCommentCount(comment.getVideoId(), -1);
    if (comment.getParentId() != null) {
      commentRepository.updateCommentCount(comment.getParentId(), -1);
    }
    List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(comment.getId());
    commentLikeRepository.deleteAll(commentLikes);
    commentRepository.delete(comment);
  }
  
  
  @Transactional
  public Video lockVideo(Video video) {
    if (video.getLocked()) {  // Already locked
      return video;
    }
  
    log.debug("Video locked: " + video.getId());
  
    if ("PUBLIC".equals(video.getVisibility())) {
      Member member = video.getMember();
      member.setPublicVideoCount(member.getPublicVideoCount() - 1);
      memberRepository.save(member);
      video.setVisibility("PRIVATE");
    }
    
    video.setLocked(true);
    return update(video);
  }
  
  @Transactional
  public Video unLockVideo(Video video) {
    log.debug("Video unlocked: " + video.getId());
    video.setLocked(false);
    return update(video);
  }
  
  @Transactional
  public void updateOrderCount(long videoId, int count) {
    videoRepository.findByIdAndDeletedAtIsNull(videoId)
        .ifPresent(video -> {
          video.setOrderCount(video.getOrderCount() + 1);
          videoRepository.save(video);
        });
  }

  /**
   * Wrap method to avoid duplication for feed aspect
   * @param video
   * @return
   */
  public Video save(Video video) {
    return videoRepository.save(video);
  }

  /**
   * Wrap method to avoid duplication for feed aspect
   * @param video
   * @return
   */
  public Video update(Video video) {
    return videoRepository.save(video);
  }

  /**
   * Wrap method to avoid duplication for feed aspect
   * @param video
   * @return
   */
  public Video saveWithDeletedAt(Video video) {
    video.setDeletedAt(new Date());
    return videoRepository.save(video);
  }
}
