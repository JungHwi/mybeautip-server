package com.jocoos.mybeautip.member.comment;

import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private final CommentRepository commentRepository;


  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
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
