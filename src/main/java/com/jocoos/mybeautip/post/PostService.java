package com.jocoos.mybeautip.post;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;


@Service
public class PostService {

  private final CommentRepository commentRepository;

  public PostService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public Slice<Comment> findCommentsByPostId(Long postId, String cursor, Pageable pageable) {
    Slice<Comment> comments = null;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = commentRepository.findByPostIdAndCreatedAtAfterAndParentIdIsNull(postId, createdAt, pageable);
    } else {
      comments = commentRepository.findByPostIdAndParentIdIsNull(postId, pageable);
    }
    return comments;
  }

  public Slice<Comment> findCommentsByParentId(Long parentId, String cursor, Pageable pageable) {
    Slice<Comment> comments = null;
    if (StringUtils.isNumeric(cursor)) {
      Date createdAt = new Date(Long.parseLong(cursor));
      comments = commentRepository.findByParentIdAndCreatedAtAfter(parentId, createdAt, pageable);
    } else {
      comments = commentRepository.findByParentId(parentId, pageable);
    }
    return comments;
  }
}
