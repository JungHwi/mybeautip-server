package com.jocoos.mybeautip.feed;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.video.Video;

@Slf4j
@Aspect
@Component
public class FeedAspect {

  private final FeedService feedService;

  public FeedAspect(FeedService feedService) {
    this.feedService = feedService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.CallbackController.createVideo(..))",
     returning = "result")
  public void onAfterReturningCreateVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
    Video video = (Video) result;
      log.debug("video: {}", video);
      feedService.feedVideo(video);
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.CallbackController.deleteVideo(..))",
    returning = "result")
  public void onAfterReturningDeleteVideo(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Video) {
      Video video = (Video) result;
      log.debug("video: {}", video);
      feedService.feedDeletedVideo(video.getId());
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.following.FollowingRepository.save(..))",
     returning = "result")
  public void onAfterReturningFollowingYou(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    if (result instanceof Following) {
      Following following = (Following) result;

      log.debug("following: {}", following);
      feedService.followMember(following.getMemberMe().getId(), following.getMemberYou().getId());
    }
  }

  @Before(value = "execution(* com.jocoos.mybeautip.member.following.FollowingRepository.delete(..))")
  public void onBeforeUnfollowingYou(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    log.debug("args: {}", joinPoint.getArgs());
    Object arg = joinPoint.getArgs()[0];
    log.debug("{}", arg);
    if (arg instanceof Following) {
      Following following = (Following) arg;

      log.debug("following: {}", following);
      feedService.unfollowMember(following.getMemberMe().getId(), following.getMemberYou().getId());
    }
  }
}
