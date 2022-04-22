package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import com.jocoos.mybeautip.support.slack.SlackService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AdminNotificationAspect {

  private final LegacyNotificationService legacyNotificationService;
  private final SlackService slackService;


  public AdminNotificationAspect(LegacyNotificationService legacyNotificationService,
                                 SlackService slackService) {
    this.legacyNotificationService = legacyNotificationService;
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

      try {
        legacyNotificationService.notifyMemberPoint(point);
      } catch (Exception e) {
        log.error("{}", e);
      }

      try {
        slackService.sendPointToMember(point);
      } catch (Exception e) {
        log.error("{}", e);
      }
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.point.MemberPointService.expiredPoint(..))",
     returning = "result")
  public void onAfterReturningExpiredPoint(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    // Ignore when duplicate createVideo
    if (result == null) {
      return;
    }

    if (result instanceof MemberPoint) {
      MemberPoint point = (MemberPoint) result;
      log.debug("point: {}", point);

      try {
        legacyNotificationService.notifyDeductMemberPoint(point);
      } catch (Exception e) {
        log.error("{}", e);
      }

      try {
        slackService.sendDeductPoint(point);
      } catch (Exception e) {
        log.error("{}", e);
      }
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.revenue.RevenuePaymentService.pay(..))",
     returning = "result")
  public void onAfterReturningRevenuePayment(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    // Ignore when duplicate createVideo
    if (result == null) {
      return;
    }

    if (result instanceof RevenuePayment) {
      RevenuePayment revenuePayment = (RevenuePayment) result;
      log.debug("revenuePayment: {}", revenuePayment);

      try {
        legacyNotificationService.notifyRevenuePayment(revenuePayment);
      } catch (Exception e) {
        log.error("{}", e);
      }
    }
  }
}
