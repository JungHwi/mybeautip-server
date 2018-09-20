package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.video.watches.VideoWatch;
import com.jocoos.mybeautip.video.watches.VideoWatchRepository;

@Slf4j
@Service
public class VideoService {

  private final MemberService memberService;
  private final VideoRepository videoRepository;
  private final VideoCommentRepository videoCommentRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final VideoWatchRepository videoWatchRepository;

  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;
  
  public VideoService(MemberService memberService,
                      VideoRepository videoRepository,
                      VideoCommentRepository videoCommentRepository,
                      VideoLikeRepository videoLikeRepository,
                      VideoWatchRepository videoWatchRepository) {
    this.memberService = memberService;
    this.videoRepository = videoRepository;
    this.videoCommentRepository = videoCommentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.videoWatchRepository = videoWatchRepository;
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

  public Slice<VideoComment> findCommentsByVideoId(Long id, String cursor, Pageable pageable) {
    Slice<VideoComment> comments;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = videoCommentRepository.findByVideoIdAndCreatedAtAfterAndParentIdIsNull(id, createdAt, pageable);
    } else {
      comments = videoCommentRepository.findByVideoIdAndParentIdIsNull(id, pageable);
    }
    return comments;
  }

  public Slice<VideoComment> findCommentsByParentId(Long parentId, String cursor, Pageable pageable) {
    Slice<VideoComment> comments;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = videoCommentRepository.findByParentIdAndCreatedAtAfter(parentId, createdAt, pageable);
    } else {
      comments = videoCommentRepository.findByParentId(parentId, pageable);
    }
    return comments;
  }

  public VideoController.VideoInfo generateVideoInfo(Video video) {
    Long likeId = null;
    Long me = memberService.currentMemberId();
    // Set likeID
    if (me != null) {
      Optional<VideoLike> optional = videoLikeRepository.findByVideoIdAndCreatedById(video.getId(), me);
      likeId = optional.map(VideoLike::getId).orElse(null);
    }
    // Set Watch count
    if ("live".equalsIgnoreCase(video.getState())) {
      long duration = new Date().getTime() - watchDuration;
      video.setWatchCount(videoWatchRepository.countByVideoIdAndModifiedAtAfter(video.getId(), new Date(duration)));
    }
    return new VideoController.VideoInfo(video, memberService.getMemberInfo(video.getMember()), likeId);
  }

  public VideoController.VideoInfo setWatcher(Long id, Member me) {
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(v -> {
        if ("live".equalsIgnoreCase(v.getState())) {
          Optional<VideoWatch> optional;
          if (me == null) {
            optional = videoWatchRepository.findByVideoIdAndUsername(v.getId(), me.getUsername());
          } else {
            optional = videoWatchRepository.findByVideoIdAndCreatedById(v.getId(), me.getId());
          }
          if (optional.isPresent()) {
            optional.get().setModifiedAt(new Date());
            videoWatchRepository.save(optional.get());
          } else {
            videoWatchRepository.save(new VideoWatch(v, me));
          }
        }
        return v;
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));
    return generateVideoInfo(video);
  }

  public VideoController.VideoInfo setWatcherWithGuest(Long id, String guestUsername) {
    Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
      .map(v -> {
        if ("live".equalsIgnoreCase(v.getState())) {
          Optional<VideoWatch> optional = videoWatchRepository.findByVideoIdAndUsername(v.getId(), guestUsername);

          if (optional.isPresent()) {
            optional.get().setModifiedAt(new Date());
            videoWatchRepository.save(optional.get());
          } else {
            videoWatchRepository.save(new VideoWatch(v, guestUsername));
          }
        }
        return v;
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));
    return generateVideoInfo(video);
  }
}
