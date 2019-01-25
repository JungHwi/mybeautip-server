package com.jocoos.mybeautip.member.comment;

import javax.transaction.Transactional;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.jocoos.mybeautip.notification.MessageService;

@Service
public class CommentService {

  private final String COMMENT_LOCKED = "comment.locked";
  
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
    comment.setComment(messageService.getMessage(COMMENT_LOCKED, Locale.KOREAN));
    commentRepository.save(comment);
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
