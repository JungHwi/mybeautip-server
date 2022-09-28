package com.jocoos.mybeautip.feed;

import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Slf4j
@Aspect
@Deprecated
public class FeedAspect {

    private final FeedService feedService;

    public FeedAspect(FeedService feedService) {
        this.feedService = feedService;
    }

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.LegacyVideoService.save(..))",
            returning = "result")
    public void onAfterReturningCreateVideo(JoinPoint joinPoint, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());

        if (result instanceof Video) {
            Video video = (Video) result;
            if ("PUBLIC".equals(video.getVisibility())) {
                log.debug("video: {}", video);
                feedService.feedVideo(video);
            }
        }
    }

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.LegacyVideoService.update(..))",
            returning = "result")
    public void onAfterReturningUpdateVideo(JoinPoint joinPoint, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());

        if (result instanceof Video) {
            Video video = (Video) result;
            if ("private".equalsIgnoreCase(video.getVisibility())) {
                log.debug("video: {}", video);
                feedService.feedDeletedVideo(video.getId());
            }
            if ("public".equalsIgnoreCase(video.getVisibility())) {
                log.debug("video: {}", video);
                feedService.feedVideo(video);
            }
        }
    }

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.video.LegacyVideoService.deleteVideo(..))",
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
