package com.jocoos.mybeautip.admin;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.support.slack.SlackService;

@Slf4j
@Aspect
@Component
public class AdminNotificationAspect {

  private final NotificationService notificationService;
  private final SlackService slackService;


  public AdminNotificationAspect(NotificationService notificationService,
                                 SlackService slackService) {
    this.notificationService = notificationService;
    this.slackService = slackService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.point.MemberPointService.presentPoint(..))",
                  returning = "result")
  public void onAfterReturningPresentPoint(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    // Ignore when duplicate createVideo
    if (result == null) {
      return;
    }

    if (result instanceof MemberPoint) {
      MemberPoint point = (MemberPoint) result;
      log.debug("point: {}", point);

      notificationService.notifyMemberPoint(point);
      slackService.sendPointToMember(point);
    }
  }
}
