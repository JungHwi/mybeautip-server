package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;

import com.jocoos.mybeautip.exception.MemberNotFoundException;

@Slf4j
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public Long currentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      log.debug("authentication: {}", authentication);

      String username = ((User) authentication.getPrincipal()).getUsername();
      if (StringUtils.isNumeric(username)) {
        Long userId = Long.parseLong(username);

        return memberRepository.findById(userId)
           .map(m -> m.getId())
           .orElseThrow(() -> new MemberNotFoundException(userId));
      } else {
        log.warn("user id can't convert to number: {}", username);
      }
    }

    return null;
  }
}
