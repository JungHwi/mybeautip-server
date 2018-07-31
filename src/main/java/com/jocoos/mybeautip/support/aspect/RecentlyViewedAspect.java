package com.jocoos.mybeautip.support.aspect;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import com.jocoos.mybeautip.member.MemberService;

@Slf4j
@Aspect
@Component
public class RecentlyViewedAspect {

  private final MemberService memberService;

  public RecentlyViewedAspect(MemberService memberService) {
    this.memberService = memberService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.PostController.addViewCount(..))",
                  returning = "result")
  public void onAfterReturningPostAddViewCountHandler(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    Object[] args = joinPoint.getArgs();
    if (args != null && args.length > 0) {
      Long postId = Long.parseLong(args[0].toString());
      log.debug("postId: {}", postId);
    }

    if (result instanceof ResponseEntity) {
      log.debug("response entity");
      ResponseEntity response = (ResponseEntity) result;
      if (response.getStatusCode() == HttpStatus.OK) {
        log.debug("response status code: {}", response.getStatusCode());
        Long memberId = memberService.currentMemberId();
        if (memberId != null) {
          // TODO : save post to be viewed
          log.debug("member id: {}", memberId);
        }
      }
    } else {
      log.warn("Unknown response type.");
    }
  }
}
