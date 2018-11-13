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

  public Slice<Comment> findCommentsByPostId(Long postId, Long cursor, Pageable pageable) {
    Slice<Comment> comments = null;
    if (cursor != null) {
      comments = commentRepository.findByPostIdAndIdGreaterThanEqualAndParentIdIsNull(postId, cursor, pageable);
    } else {
      comments = commentRepository.findByPostIdAndParentIdIsNull(postId, pageable);
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
}
