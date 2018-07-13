package com.jocoos.mybeautip.audit;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.MemberRepository;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Long> {

  @Autowired
  private MemberRepository memberRepository;

  @Override
  public Optional<Long> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      log.debug("authentication: {}", authentication);
      log.debug("principal: {}", authentication.getPrincipal());

      String username = ((User) authentication.getPrincipal()).getUsername();
      Long userId = Long.parseLong(username);

      return memberRepository.findById(userId)
         .map(m -> Optional.of(m.getId()))
         .orElseThrow(() -> new MemberNotFoundException());
    }

    return Optional.empty();
  }
}
