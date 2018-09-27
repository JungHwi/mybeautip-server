package com.jocoos.mybeautip.notification;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;

@Slf4j
@Aspect
@Component
public class NotificationAspect {

  private final NotificationService notificationService;

  public NotificationAspect(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.CallbackController.createVideo(..))",
    returning = "result")
  public void onAfterReturningCreateVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
      Video video = (Video) result;
      log.debug("video: {}", video);
      notificationService.notifyCreateVideo(video);
    }
  }

  @Before(value = "execution(* com.jocoos.mybeautip.member.following.FollowingRepository.save(..))")
  public void onBeforeSaveFollowingMember(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof Following) {
      Following f = (Following) o;
      log.debug("following: {}", f);
      notificationService.notifyFollowMember(f);
    }
  }

  @After(value = "execution(* com.jocoos.mybeautip.member.comment.CommentRepository.save(..))")
  public void onAfterSaveComment(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof Comment) {
      Comment comment = (Comment) o;
      log.debug("comment: {}", comment);
      if (comment.getParentId() != null) {
        notificationService.notifyAddCommentReply(comment);
      } else {
        notificationService.notifyAddComment(comment);
      }
    }
  }

  @After(value = "execution(* com.jocoos.mybeautip.video.VideoLikeRepository.save(..))")
  public void onAfterSaveVideoLike(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof VideoLike) {
      VideoLike videoLike = (VideoLike) o;
      log.debug("video like: {}", videoLike);
      notificationService.notifyAddVideoLike(videoLike);
    }
  }

  @After(value = "execution(* com.jocoos.mybeautip.member.comment.CommentLikeRepository.save(..))")
  public void onAfterSaveCommentLike(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    Object o = joinPoint.getArgs()[0];
    if (o instanceof CommentLike) {
      CommentLike commentLike = (CommentLike) o;
      log.debug("comment like: {}", commentLike);
      notificationService.notifyAddCommentLike(commentLike);
    }
  }
}
