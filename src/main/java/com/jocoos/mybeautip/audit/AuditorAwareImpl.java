package com.jocoos.mybeautip.audit;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.MemberService;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Long> {

  @Autowired
  private MemberService memberService;

  @Override
  public Optional<Long> getCurrentAuditor() {
    return Optional.ofNullable(memberService.currentMemberId());
  }
}
