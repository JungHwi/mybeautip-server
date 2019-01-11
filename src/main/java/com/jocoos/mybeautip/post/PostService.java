package com.jocoos.mybeautip.post;

import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


@Service
public class PostService {

  private final CommentRepository commentRepository;

  public PostService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public Slice<Comment> findCommentsByPostId(Long id, Long cursor, Pageable pageable, String direction) {
    Slice<Comment> comments;
    if (cursor != null) {
      if ("next".equals(direction)) {
        comments = commentRepository.findByPostIdAndIdGreaterThanEqualAndParentIdIsNull(id, cursor, pageable);
      } else {
        comments = commentRepository.findByPostIdAndIdLessThanEqualAndParentIdIsNull(id, cursor, pageable);
      }
    } else {
      comments = commentRepository.findByPostIdAndParentIdIsNull(id, pageable);
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
  
  public String getPostCategoryName(int category) {
    switch (category) {
      case 1:
        return "post.category_trend";
      case 2:
        return "post.category_cardnews";
      case 3:
        return "post.category_event";
      case 4:
        return "post.category_notice";
      case 5:
        return "post.category_motd";
      case 6:
        return "post.category_curation";
      default:
        return "post";
    }
  }
}
