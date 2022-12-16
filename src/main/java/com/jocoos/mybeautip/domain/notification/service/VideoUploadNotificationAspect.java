package com.jocoos.mybeautip.domain.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jocoos.mybeautip.domain.notification.service.impl.VideoUploadNotificationService;
import com.jocoos.mybeautip.support.AsyncService;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import static com.jocoos.mybeautip.domain.video.code.VideoStatus.OPEN;


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
      if (verifyPublicVideo(video)) {
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

  // FIXME PLAN D 때 AOP 리팩토링한 것으로 교체할 것
  private boolean verifyPublicVideo(Video video) {
    return "UPLOADED".equals(video.getType()) &&
        "VOD".equals(video.getState())
            && "PUBLIC".equals(video.getVisibility())
            && OPEN.equals(video.getStatus());
  }

  @Async
  public void send(Video video) {
    asyncService.run(() -> videoUploadNotificationService.send(video));
  }
}
