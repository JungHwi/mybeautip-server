package com.jocoos.mybeautip.member.comment;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.jocoos.mybeautip.comment.CreateCommentRequest;
import com.jocoos.mybeautip.comment.UpdateCommentRequest;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.VideoRepository;

@Service
public class CommentService {

  public static final int COMMENT_TYPE_VIDEO = 1;
  public static final int COMMENT_TYPE_POST = 2;
  private final String COMMENT_LOCK_MESSAGE = "comment.lock_message";
  
  private final TagService tagService;
  private final MessageService messageService;
  private final MentionService mentionService;
  private final NotificationService notificationService;
  private final CommentRepository commentRepository;
  private final VideoRepository videoRepository;
  
  public CommentService(TagService tagService,
                        MessageService messageService,
                        MentionService mentionService,
                        NotificationService notificationService,
                        CommentRepository commentRepository,
                        VideoRepository videoRepository) {
    this.tagService = tagService;
    this.messageService = messageService;
    this.mentionService = mentionService;
    this.notificationService = notificationService;
    this.commentRepository = commentRepository;
    this.videoRepository = videoRepository;
  }
  
  @Transactional
  public void lockComment(Comment comment) {
    comment.setLocked(true);
    comment.setOriginalComment(comment.getComment());
    comment.setComment(messageService.getMessage(COMMENT_LOCK_MESSAGE, Locale.KOREAN));
    commentRepository.save(comment);
  }
  
  @Transactional
  public Comment addComment(CreateCommentRequest request, int type, long id) {
    if (request.getParentId() != null) {
      commentRepository.findById(request.getParentId())
          .ifPresent(parent -> commentRepository.updateCommentCount(parent.getId(), 1));
    }
  
    Comment comment = new Comment();
    if (type == COMMENT_TYPE_VIDEO) {
      comment.setVideoId(id);
    }
    if (type == COMMENT_TYPE_POST) {
      comment.setPostId(id);
    }
    
    BeanUtils.copyProperties(request, comment);
    videoRepository.updateCommentCount(id, 1);
    comment = commentRepository.save(comment);
    
    tagService.touchRefCount(comment.getComment());
    tagService.addHistory(comment.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());
  
    List<MentionTag> mentionTags = request.getMentionTags();
    if (mentionTags != null && mentionTags.size() > 0) {
      if (type == COMMENT_TYPE_VIDEO) {
        mentionService.updateVideoCommentWithMention(comment, mentionTags);
      }
      
      if (type == COMMENT_TYPE_POST) {
        mentionService.updatePostCommentWithMention(comment, mentionTags);
      }
    } else {
      notificationService.notifyAddComment(comment);
    }
    
    return comment;
  }
  
  @Transactional
  public Comment updateComment(UpdateCommentRequest request, Comment comment) {
    tagService.touchRefCount(request.getComment());
    tagService.updateHistory(comment.getComment(), request.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());
  
    comment.setComment(request.getComment());
    return commentRepository.save(comment);
  }
}
