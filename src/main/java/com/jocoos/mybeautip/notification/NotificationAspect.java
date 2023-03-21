package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.VideoLike;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class NotificationAspect {

    private final LegacyNotificationService legacyNotificationService;
    private final SlackService slackService;

    @After(value = "execution(* com.jocoos.mybeautip.video.VideoLikeRepository.save(..))")
    public void onAfterSaveVideoLike(JoinPoint joinPoint) {
        log.debug("joinPoint: {}", joinPoint.toLongString());
        Object o = joinPoint.getArgs()[0];
        if (o instanceof VideoLike videoLike) {
            log.debug("video like: {}", videoLike);
            legacyNotificationService.notifyAddVideoLike(videoLike);
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

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.order.OrderService.cancelOrderInquireByAdmin(..))",
            returning = "result")
    public void onAfterReturningOrderCancelByAdmin(JoinPoint joinPoint, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());

        if (result instanceof OrderInquiry) {
            OrderInquiry orderInquiry = (OrderInquiry) result;
            log.debug("orderInquiry: {}", orderInquiry);
            slackService.sendForOrderCancelByAdmin(orderInquiry);
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
}
