package com.jocoos.mybeautip.member.comment;

import javax.transaction.Transactional;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.jocoos.mybeautip.notification.MessageService;

@Service
public class CommentService {

  private final String COMMENT_LOCK_MESSAGE = "comment.lock_message";
  
  private final MessageService messageService;
  private final CommentRepository commentRepository;


  public CommentService(MessageService messageService,
                        CommentRepository commentRepository) {
    this.messageService = messageService;
    this.commentRepository = commentRepository;
  }
  
  @Transactional
  public void lockComment(Comment comment) {
    comment.setLocked(true);
    comment.setOriginalComment(comment.getComment());
    comment.setComment(messageService.getMessage(COMMENT_LOCK_MESSAGE, Locale.KOREAN));
    commentRepository.save(comment);
  }
  
  @Transactional
  public void updateCount(Comment comment, int i) {
    commentRepository.updateCommentCount(comment.getId(), 1);
  }

  /**
   * Wrap method to avoid duplication for notification aspect
   * @param comment
   * @return
   */
  public Comment save(Comment comment) {
    return commentRepository.save(comment);
  }

  /**
   * Wrap method to avoid duplication for notification aspect
   * @param comment
   * @return
   */
  public Comment update(Comment comment) {
    return commentRepository.save(comment);
  }
}
