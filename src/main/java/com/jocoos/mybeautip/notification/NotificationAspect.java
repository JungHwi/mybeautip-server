package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.comment.CommentReport;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.report.VideoReport;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class NotificationAspect {

    private final LegacyNotificationService legacyNotificationService;
    private final FeedService feedService;
    private final SlackService slackService;


    public NotificationAspect(LegacyNotificationService legacyNotificationService,
                              SlackService slackService,
                              InstantNotificationConfig instantNotificationConfig,
                              ThreadPoolTaskScheduler taskScheduler,
                              FeedService feedService) {
        this.legacyNotificationService = legacyNotificationService;
        this.slackService = slackService;
        this.feedService = feedService;
    }

    @Deprecated
    public void onAfterReturningStartVideo(JoinPoint joinPoint, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());

        // Ignore when duplicate createVideo
        if (result == null) {
            return;
        }

        if (result instanceof Video) {
            Video video = (Video) result;
            log.debug("video: {}", video);
            if ("PUBLIC".equals(video.getVisibility())) {
//                feedService.feedVideo(video);
            }

            slackService.sendForVideo(video);

            // See VideoUploadNotificationService.occurs 참고.
//      if ("UPLOADED".equals(video.getType()) && "VOD".equals(video.getState())) {
//        legacyNotificationService.notifyUploadedMyVideo(video);
//      }
        }
    }

    @After(value = "execution(* com.jocoos.mybeautip.video.VideoLikeRepository.save(..))")
    public void onAfterSaveVideoLike(JoinPoint joinPoint) {
        log.debug("joinPoint: {}", joinPoint.toLongString());
        Object o = joinPoint.getArgs()[0];
        if (o instanceof VideoLike) {
            VideoLike videoLike = (VideoLike) o;
            log.debug("video like: {}", videoLike);
            legacyNotificationService.notifyAddVideoLike(videoLike);
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

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.comment.CommentReportRepository.save(..))",
            returning = "result")
    public void onAfterReturningReportComment(JoinPoint joinPoint, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());

        if (result instanceof CommentReport) {
            CommentReport commentReport = (CommentReport) result;
            log.debug("commentReport: {}", commentReport);
            slackService.sendForReportComment(commentReport);
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

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.block.BlockRepository.save(..))",
            returning = "result")
    public void onAfterReturningBlockMember(JoinPoint joinPoint, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());

        if (result instanceof Block) {
            Block memberBlock = (Block) result;
            log.debug("memberBlock: {}", memberBlock);
            slackService.sendForBlockMember(memberBlock);
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
