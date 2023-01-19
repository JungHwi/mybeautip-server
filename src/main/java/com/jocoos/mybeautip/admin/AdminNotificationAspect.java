package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.support.slack.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class AdminNotificationAspect {

    private final SlackService slackService;

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
                slackService.sendPointToMember(point);
            } catch (Exception e) {
                log.error("{}", e);
            }
        }
    }
}
