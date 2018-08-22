package com.jocoos.mybeautip.video;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;


@Service
public class VideoService {

  private final VideoCommentRepository videoCommentRepository;
  
  public VideoService(VideoCommentRepository videoCommentRepository) {
    this.videoCommentRepository = videoCommentRepository;
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
}
