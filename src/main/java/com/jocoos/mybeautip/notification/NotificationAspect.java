package com.jocoos.mybeautip.notification;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.post.PostComment;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoComment;

@Slf4j
@Aspect
@Component
public class NotificationAspect {

  private final NotificationService notificationService;

  public NotificationAspect(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.VideoRepository.save(..))",
     returning = "result")
  public void onAfterReturningCreateVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
      Video video = (Video) result;
      log.debug("video: {}", video);
      notificationService.notifyCreateVideo(video);
    }
  }

  @After(value = "execution(* com.jocoos.mybeautip.video.VideoCommentRepository.save(..))")
  public void onAfterVideoSaveComment(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof VideoComment) {
      VideoComment comment = (VideoComment) o;
      log.debug("video comment: {}", comment);
      if (comment.getParentId() != null) {
        notificationService.notifyAddVideoCommentReply(comment);
      } else {
        notificationService.notifyAddVideoComment(comment);
      }
    }
  }

  @Before(value = "execution(* com.jocoos.mybeautip.video.VideoCommentRepository.save(..))")
  public void onBeforeSaveFollowingMember(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof Following) {
      Following f = (Following) o;
      log.debug("following: {}", f);
      notificationService.notifyFollowMember(f);
    }
  }

  @After(value = "execution(* com.jocoos.mybeautip.post.PostCommentRepository.save(..))")
  public void onAfterPostSaveComment(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof PostComment) {
      PostComment comment = (PostComment) o;
      log.debug("post comment: {}", comment);
      if (comment.getParentId() != null) {
        notificationService.notifyAddPostCommentReply(comment);
      } else {
        notificationService.notifyAddPostComment(comment);
      }
    }
  }


  // TODO : Not implement video comment like
  // TODO : Not implement post comment like

  // TODO : How to get video like count
}
