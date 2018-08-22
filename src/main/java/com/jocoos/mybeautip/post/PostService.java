package com.jocoos.mybeautip.post;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;


@Service
public class PostService {

  private final PostCommentRepository postCommentRepository;

  public PostService(PostCommentRepository postCommentRepository) {
    this.postCommentRepository = postCommentRepository;
  }

  public Slice<PostComment> findCommentsByPostId(Long postId, String cursor, Pageable pageable) {
    Slice<PostComment> comments = null;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = postCommentRepository.findByPostIdAndCreatedAtAfterAndParentIdIsNull(postId, createdAt, pageable);
    } else {
      comments = postCommentRepository.findByPostIdAndParentIdIsNull(postId, pageable);
    }
    return comments;
  }

  public Slice<PostComment> findCommentsByParentId(Long parentId, String cursor, Pageable pageable) {
    Slice<PostComment> comments = null;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = postCommentRepository.findByParentIdAndCreatedAtAfter(parentId, createdAt, pageable);
    } else {
      comments = postCommentRepository.findByParentId(parentId, pageable);
    }
    return comments;
  }
}
