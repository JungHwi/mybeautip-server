package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.VideoController;


@Service
public class VideoService {

  private final MemberService memberService;
  private final VideoCommentRepository videoCommentRepository;
  private final VideoLikeRepository videoLikeRepository;
  
  public VideoService(MemberService memberService,
                      VideoCommentRepository videoCommentRepository,
                      VideoLikeRepository videoLikeRepository) {
    this.memberService = memberService;
    this.videoCommentRepository = videoCommentRepository;
    this.videoLikeRepository = videoLikeRepository;
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
    if (me != null) {
      Optional<VideoLike> optional = videoLikeRepository.findByVideoIdAndCreatedBy(video.getId(), me);
      likeId = optional.map(VideoLike::getId).orElse(null);
    }
    return new VideoController.VideoInfo(video, memberService.getMemberInfo(video.getMember()), likeId);
  }
}
