package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.watches.VideoWatch;
import com.jocoos.mybeautip.video.watches.VideoWatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class VideoService {

  private final MemberService memberService;
  private final MessageService messageService;
  private final TagService tagService;
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final VideoWatchRepository videoWatchRepository;
  private final BlockRepository blockRepository;
  private final MemberRepository memberRepository;

  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;

  private static final String VIDEO_NOT_FOUND = "video.not_found";
  
  public VideoService(MemberService memberService,
                      MessageService messageService,
                      TagService tagService,
                      VideoRepository videoRepository,
                      CommentRepository commentRepository,
                      VideoLikeRepository videoLikeRepository,
                      VideoWatchRepository videoWatchRepository,
                      BlockRepository blockRepository,
                      MemberRepository memberRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.tagService = tagService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.videoWatchRepository = videoWatchRepository;
    this.blockRepository = blockRepository;
    this.memberRepository = memberRepository;
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

  public Slice<Comment> findCommentsByVideoId(Long id, Long cursor, Pageable pageable) {
    Slice<Comment> comments;
    if (cursor != null) {
      comments = commentRepository.findByVideoIdAndIdGreaterThanEqualAndParentIdIsNull(id, cursor, pageable);
    } else {
      comments = commentRepository.findByVideoIdAndParentIdIsNull(id, pageable);
    }
    return comments;
  }

  public Slice<Comment> findCommentsByParentId(Long parentId, Long cursor, Pageable pageable) {
    Slice<Comment> comments;
    if (cursor != null) {
      comments = commentRepository.findByParentIdAndIdGreaterThanEqual(parentId, cursor, pageable);
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

  public VideoController.VideoInfo setWatcher(Long id, Member me, String lang) {
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(v -> {
        if ("live".equalsIgnoreCase(v.getState())) {
          Optional<VideoWatch> optional;
          optional = videoWatchRepository.findByVideoIdAndCreatedById(v.getId(), me.getId());
          if (optional.isPresent()) {
            optional.get().setModifiedAt(new Date());
            videoWatchRepository.save(optional.get());
          } else {
            videoWatchRepository.save(new VideoWatch(v, me));
            videoRepository.updateTotalWatchCount(v.getId(), 1);
            v.setTotalWatchCount(v.getTotalWatchCount() + 1);
          }
        }
        return v;
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
    return generateVideoInfo(video);
  }

  public VideoController.VideoInfo setWatcherWithGuest(Long id, String guestUsername, String lang) {
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(v -> {
        if ("live".equalsIgnoreCase(v.getState())) {
          Optional<VideoWatch> optional = videoWatchRepository.findByVideoIdAndUsername(v.getId(), guestUsername);

          if (optional.isPresent()) {
            optional.get().setModifiedAt(new Date());
            videoWatchRepository.save(optional.get());
          } else {
            videoWatchRepository.save(new VideoWatch(v, guestUsername));
            videoRepository.updateTotalWatchCount(v.getId(), 1);
            v.setTotalWatchCount(v.getTotalWatchCount() + 1);
          }
        }
        return v;
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));
    return generateVideoInfo(video);
  }
  
  public Video deleteVideo(long memberId, String videoKey) {
    return videoRepository.findByVideoKeyAndDeletedAtIsNull(videoKey)
        .map(v -> {
          if (v.getMember().getId() != memberId) {
            throw new BadRequestException("invalid_user_id", "Invalid user_id: " + memberId);
          }
          tagService.decreaseRefCount(v.getTagInfo());
          v.setDeletedAt(new Date());
          saveWithDeletedAt(v);
          videoLikeRepository.deleteByVideoId(v.getId());
          if ("PUBLIC".equals(v.getVisibility())) {
            memberRepository.updateVideoCount(v.getMember().getId(), v.getMember().getVideoCount() - 1);
          }
          memberRepository.updateTotalVideoCount(v.getMember().getId(), v.getMember().getTotalVideoCount() - 1);
          return v;
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, videoKey: " + videoKey));
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
    return videoRepository.save(video);
  }
}
