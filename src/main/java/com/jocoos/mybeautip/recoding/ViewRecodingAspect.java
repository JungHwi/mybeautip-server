package com.jocoos.mybeautip.recoding;

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
public class ViewRecodingAspect {

  private final MemberService memberService;
  private final ViewRecodingService viewRecodingService;

  public ViewRecodingAspect(MemberService memberService,
                            ViewRecodingService viewRecodingService) {
    this.memberService = memberService;
    this.viewRecodingService = viewRecodingService;
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.PostController.addViewCount(..))",
                  returning = "result")
  public void onAfterReturningPostAddViewCountHandler(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    Object[] args = joinPoint.getArgs();
    if (args != null && args.length > 0) {
      Long postId = Long.parseLong(args[0].toString());
      log.debug("postId: {}", postId);

      if (result instanceof ResponseEntity) {
        log.debug("response entity");
        ResponseEntity response = (ResponseEntity) result;
        if (response.getStatusCode() == HttpStatus.OK) {
          log.debug("response status code: {}", response.getStatusCode());
          if (memberService.currentMemberId() != null) {
            viewRecodingService.insertOrUpdate(String.valueOf(postId), ViewRecoding.CATEGORY_POST);
          }
        }
      } else {
        log.warn("Unknown response type.");
      }
    } else {
      log.debug("joinPoint: {}", joinPoint.toLongString());
      log.warn("Invalid args length.");
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.GoodsController.getGoodsDetail(..))",
     returning = "result")
  public void onAfterReturningGoodsDetailHandler(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    Object[] args = joinPoint.getArgs();
    if (args != null && args.length > 0) {
      String goodsNo = args[0].toString();
      log.debug("goodsNo: {}", goodsNo);

      if (result instanceof ResponseEntity) {
        log.debug("response entity");
        ResponseEntity response = (ResponseEntity) result;
        if (response.getStatusCode() == HttpStatus.OK) {
          log.debug("response status code: {}", response.getStatusCode());
          if (memberService.currentMemberId() != null) {
            viewRecodingService.insertOrUpdate(goodsNo, ViewRecoding.CATEGORY_GOODS);
          }
        }
      } else {
        log.warn("Unknown response type.");
      }
    } else {
      log.debug("joinPoint: {}", joinPoint.toLongString());
      log.warn("Invalid args length.");
    }
  }

  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.VideoController.addView(..)) " +
                          "|| execution(* com.jocoos.mybeautip.restapi.VideoController.joinWatch(..))",
    returning = "result")
  public void onAfterReturningVideoAddViewCountHandler(JoinPoint joinPoint, Object result) {
    log.debug("joinPoint: {}", joinPoint.toLongString());

    Object[] args = joinPoint.getArgs();
    if (args != null && args.length > 0) {
      Long videoId = Long.parseLong(args[0].toString());
      log.debug("videoId: {}", videoId);

      if (result instanceof ResponseEntity) {
        log.debug("response entity");
        ResponseEntity response = (ResponseEntity) result;
        if (response.getStatusCode() == HttpStatus.OK) {
          log.debug("response status code: {}", response.getStatusCode());
          if (memberService.currentMemberId() != null) {
            viewRecodingService.insertOrUpdate(String.valueOf(String.valueOf(videoId)), ViewRecoding.CATEGORY_VIDEO);
          }
        }
      } else {
        log.warn("Unknown response type.");
      }
    } else {
      log.debug("joinPoint: {}", joinPoint.toLongString());
      log.warn("Invalid args length.");
    }
  }
}
