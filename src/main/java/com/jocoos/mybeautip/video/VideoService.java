package com.jocoos.mybeautip.video;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.flywaydb.core.internal.util.StringUtils;

@Service
public class VideoService {

  private final VideoCommentRepository videoCommentRepository;
  
  public VideoService(VideoCommentRepository videoCommentRepository) {
    this.videoCommentRepository = videoCommentRepository;
  }

  public Slice<VideoComment> findCommentsByVideoKey(Long videoKey, String cursor, Pageable pageable) {
    Slice<VideoComment> comments = null;
    if (StringUtils.hasLength(cursor) && StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = videoCommentRepository.findByVideoKeyAndCreatedAtAfterAndParentIdIsNull(videoKey, createdAt, pageable);
    } else {
      comments = videoCommentRepository.findByVideoKeyAndParentIdIsNull(videoKey, pageable);
    }
    return comments;
  }

  public Slice<VideoComment> findCommentsByParentId(Long parentId, String cursor, Pageable pageable) {
    Slice<VideoComment> comments = null;
    if (StringUtils.hasLength(cursor) && StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = videoCommentRepository.findByParentIdAndCreatedAtAfter(parentId, createdAt, pageable);
    } else {
      comments = videoCommentRepository.findByParentId(parentId, pageable);
    }
    return comments;
  }
}
