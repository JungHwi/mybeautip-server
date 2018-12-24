package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SearchAspect {
  
  private final MemberService memberService;
  private final KeywordService keywordService;
  
  public SearchAspect(MemberService memberService, KeywordService keywordService) {
    this.memberService = memberService;
    this.keywordService = keywordService;
  }
  
  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.VideoController.searchVideos(..))")
  public void onAfterReturningSearchVideo(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    String keyword = (String) joinPoint.getArgs()[2];
  
    if (StringUtils.isNotBlank(keyword)) {
      keywordService.logHistoryAndUpdateStats(keyword, KeywordService.KeywordCategory.VIDEO, memberService.currentMember());
    }
  }
  
  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.MemberController.getMembers(..))")
  public void onAfterReturningGetMembers(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    String keyword = (String) joinPoint.getArgs()[2];
  
    if (StringUtils.isNotBlank(keyword)) {
      keywordService.logHistoryAndUpdateStats(keyword, KeywordService.KeywordCategory.MEMBER, memberService.currentMember());
    }
  }
  
  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.PostController.getPosts(..))")
  public void onAfterReturningGetPosts(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    String keyword = (String) joinPoint.getArgs()[2];
  
    if (StringUtils.isNotBlank(keyword)) {
      keywordService.logHistoryAndUpdateStats(keyword, KeywordService.KeywordCategory.POST, memberService.currentMember());
    }
  }
  
  @AfterReturning(value = "execution(* com.jocoos.mybeautip.restapi.GoodsController.getGoodsList(..))")
  public void onAfterReturningGetGoodsList(JoinPoint joinPoint) {
    log.debug("joinPoint: {}", joinPoint.toLongString());
    String keyword = (String) joinPoint.getArgs()[2];
  
    if (StringUtils.isNotBlank(keyword)) {
      keywordService.logHistoryAndUpdateStats(keyword, KeywordService.KeywordCategory.GOODS, memberService.currentMember());
    }
  }
}