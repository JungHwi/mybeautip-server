package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.report.VideoReport;
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
  private final SlackService slackService;

  public NotificationAspect(NotificationService notificationService,
                            SlackService slackService) {
    this.notificationService = notificationService;
    this.slackService = slackService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.CallbackController.createVideo(..))",
    returning = "result")
  public void onAfterReturningCreateVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
      Video video = (Video) result;
      log.debug("video: {}", video);
      if ("PUBLIC".equals(video.getVisibility())) {
        notificationService.notifyCreateVideo(video);
      }

      slackService.sendForVideo(video);

      if ("UPLOADED".equals(video.getType()) && "VOD".equals(video.getState())) {
        notificationService.notifyUploadedMyVideo(video);
      }
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

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.report.VideoReportRepository.save(..))",
      returning = "result")
  public void onAfterReturningReportVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof VideoReport) {
      VideoReport videoReport = (VideoReport) result;
      log.debug("videoReport: {}", videoReport);
      slackService.sendForReportVideo(videoReport);
    }
  }


  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.report.ReportRepository.save(..))",
      returning = "result")
  public void onAfterReturningReportMember(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Report) {
      Report memberReport = (Report) result;
      log.debug("memberReport: {}", memberReport);
      slackService.sendForReportMember(memberReport);
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.order.OrderService.notifyPayment(..))",
      returning = "result")
  public void onAfterReturningOrder(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Order) {
      Order order = (Order) result;
      log.debug("order: {}", order);
      slackService.sendForOrder(order);
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.order.OrderService.cancelOrderInquire(..))",
      returning = "result")
  public void onAfterReturningOrderCancel(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof OrderInquiry) {
      OrderInquiry orderInquiry = (OrderInquiry) result;
      log.debug("orderInquiry: {}", orderInquiry);
      slackService.sendForOrderCancel(orderInquiry);
    }
  }
  
  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.order.OrderService.inquiryExchangeOrReturn(..))",
      returning = "result")
  public void onAfterReturningOrderExchangeOrReturn(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    
    if (result instanceof OrderInquiry) {
      OrderInquiry orderInquiry = (OrderInquiry) result;
      log.debug("orderInquiry: {}", orderInquiry);
      slackService.SendForOrderExchangeOrReturn(orderInquiry);
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.log.MemberLeaveLogRepository.save(..))",
      returning = "result")
  public void onAfterReturningAddDeleteMemberLog(JoinPoint joinPoint, Object result) {
    log.info("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof MemberLeaveLog) {
      MemberLeaveLog memberLeaveLog = (MemberLeaveLog) result;
      log.info("video: {}", memberLeaveLog);
      slackService.sendForDeleteMember(memberLeaveLog);
    }
  }
}
