package com.jocoos.mybeautip.post;

import java.util.List;

import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;

@Slf4j
@Service
public class PostService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final PostLikeRepository postLikeRepository;

  public PostService(CommentRepository commentRepository,
                     PostRepository postRepository,
                     CommentLikeRepository commentLikeRepository,
                     PostLikeRepository postLikeRepository) {
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.postLikeRepository = postLikeRepository;
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
  
  @Transactional
  public void deleteComment(Comment comment) {
    try {
      postRepository.updateCommentCount(comment.getPostId(), -1);
    } catch (DataException e) {
      log.warn("DataException throws updateCommentCount: comment: {}, exception: {}", comment, e.getMessage());
    }
    if (comment.getParentId() != null) {
      try {
        commentRepository.updateCommentCount(comment.getParentId(), -1);
      } catch (DataException e) {
        log.warn("DataException throws updateCommentCount: comment: {}, exception: {}", comment, e.getMessage());
      }
    }
    List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(comment.getId());
    commentLikeRepository.deleteAll(commentLikes);
    commentRepository.delete(comment);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public PostLike likePost(Post post) {
    postRepository.updateLikeCount(post.getId(), 1);
    post.setLikeCount(post.getLikeCount() + 1);
    return postLikeRepository.save(new PostLike(post));
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void unLikePost(PostLike liked) {
    postLikeRepository.delete(liked);
    try {
      postRepository.updateLikeCount(liked.getPost().getId(), -1);
    } catch (DataException e) {
      log.warn("DataException throws updatePostLikeCount: postLike: {}, exception: {}", liked, e.getMessage());
    }
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public CommentLike likeCommentPost(Comment comment) {
    commentRepository.updateLikeCount(comment.getId(), 1);
    return commentLikeRepository.save(new CommentLike(comment));
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void unLikeCommentPost(CommentLike liked) {
    commentLikeRepository.delete(liked);
    try {
      commentRepository.updateLikeCount(liked.getComment().getId(), -1);
    } catch (DataException e) {
      log.warn("DataException throws updateCommentLikeCount: comment: {}, exception: {}", liked, e.getMessage());
    }
  }
  
  @Transactional
  public void updateViewCount(Post post, int i) {
    postRepository.updateViewCount(post.getId(), i);
  }
}
