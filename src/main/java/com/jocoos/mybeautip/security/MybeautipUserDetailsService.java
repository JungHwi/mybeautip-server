package com.jocoos.mybeautip.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.MemberRepository;


@Service
@Slf4j
public class MybeautipUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  public MybeautipUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("load user by username: {}", username);
    if (StringUtils.startsWith(username, "guest:")) {
      return createGuestUserDetails(username);
    }

    return memberRepository.findByIdAndDeletedAtIsNull(Long.parseLong(username))
        .map(m -> {
          switch (m.getLink()) {
            case 0: {
              return new MyBeautipUserDetails(m, "ROLE_ADMIN");
            }
            case 32: {
              return new MyBeautipUserDetails(m, "ROLE_STORE");
            }
            default: {
              return new MyBeautipUserDetails(m);
            }
          }
        })
        .orElseThrow(() -> new AuthenticationException("username not found"));
  }

  private MyBeautipUserDetails createGuestUserDetails(String username) {
    return new MyBeautipUserDetails(username, "ROLE_GUEST");
  }
}
