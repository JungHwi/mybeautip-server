package com.jocoos.mybeautip.domain.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import com.jocoos.mybeautip.domain.member.service.MemberService;
import com.jocoos.mybeautip.domain.notification.service.impl.VideoUploadNotificationService;
import com.jocoos.mybeautip.support.AsyncService;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VideoUploadNotificationAspect {

  private final SlackService slackService;
  private final VideoUploadNotificationService videoUploadNotificationService;
  private final AsyncService asyncService;

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.VideoUpdateService.created(..))",
      returning = "result")
  public void occurs(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
      Video video = (Video) result;
      if (verify(video)) {
        send(video);
      }

      slackService.sendForVideo(video);
    } else {
      log.error("Must be Video. But this object is > " + result);
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.VideoUpdateService.updated(..))",
      returning = "result")
  public void updateVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
      Video video = (Video) result;
      log.debug("video: {}", video);
      if (video.isFirstOpen()) {
        send(video);

        slackService.makeVideoPublic(video);
      }
    }
  }

  @Around(value = "execution(* com.jocoos.mybeautip.video.VideoService.*(..))")
  public Object time(ProceedingJoinPoint joinPoint) throws Throwable {
    StopWatch stopwatch = StopWatch.createStarted();
    Object proceed = joinPoint.proceed();
    stopwatch.stop();
    log.debug("{}, time: {} ms", joinPoint.toLongString(), stopwatch.getTime(TimeUnit.MILLISECONDS));
    return proceed;
  }

  private boolean verify(Video video) {
    return "UPLOADED".equals(video.getType()) &&
        "VOD".equals(video.getState());
  }

//  private boolean verify(Video video) {
//    return "UPLOADED".equals(video.getType()) &&
//        "VOD".equals(video.getState()) && "PUBLIC".equals(video.getVisibility());
//  }

  @Async
  public void send(Video video) {
    asyncService.run(() -> videoUploadNotificationService.send(video));
  }
}
