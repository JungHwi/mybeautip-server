package com.jocoos.mybeautip.post;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.support.AttachmentService;

@Slf4j
@Service
public class PostService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final PostLikeRepository postLikeRepository;
  private final PostReportRepository postReportRepository;
  private final AttachmentService attachmentService;

  public PostService(CommentRepository commentRepository,
                     PostRepository postRepository,
                     CommentLikeRepository commentLikeRepository,
                     PostLikeRepository postLikeRepository,
                     PostReportRepository postReportRepository,
                     AttachmentService attachmentService) {

    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.postLikeRepository = postLikeRepository;
    this.postReportRepository = postReportRepository;
    this.attachmentService = attachmentService;
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
    postRepository.findById(comment.getPostId()).ifPresent(
        post -> {
          if (post.getCommentCount() > 0) {
            postRepository.updateCommentCount(comment.getPostId(), -1);
          }
        }
    );
    
    if (comment.getParentId() != null) {
      postRepository.findById(comment.getParentId()).ifPresent(
          parentComment -> {
            if (parentComment.getCommentCount() > 0) {
              commentRepository.updateCommentCount(parentComment.getId(), -1);
            }
          }
      );
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
    if (liked.getPost().getLikeCount() > 0) {
      postRepository.updateLikeCount(liked.getPost().getId(), -1);
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
    if (liked.getComment().getCommentCount() > 0) {
      commentRepository.updateLikeCount(liked.getComment().getId(), -1);
    }
  }
  
  @Transactional
  public void updateViewCount(Post post, int i) {
    postRepository.updateViewCount(post.getId(), i);
  }

  @Transactional
  public Post reportPost(Post post, Member me, int reasonCode, String reason) {
    postReportRepository.save(new PostReport(post, me, reasonCode, reason));
    post.setReportCount(post.getReportCount() + 1);
    return postRepository.save(post);
  }

  @Transactional
  public Post savePost(Post post, List<MultipartFile> files) {
    postRepository.save(post);

    List<String> attachments = null;
    try {
      String keyPath = String.format("posts/%s", post.getId());
      attachments = attachmentService.upload(files, keyPath);
    } catch (IOException e) {
      throw new BadRequestException("post_image_upload_fail", "state_required");
    }

    if(attachments != null && attachments.size() > 0) {
      int seq = 0;
      Set<PostContent> contents = Sets.newHashSet();
      for (String attachment: attachments) {
        contents.add(new PostContent(seq++, 1, attachment));

      }
      post.setContents(contents);
      postRepository.save(post);
    }

    return post;
  }
}
