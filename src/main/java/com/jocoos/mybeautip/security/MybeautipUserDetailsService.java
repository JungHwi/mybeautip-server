package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


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

    switch (username) {
      case "0": {
        return new User("admin", "", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
      }
      case "guest": {
        return createGuestUserDetails();
      }
      default: {
        return memberRepository.findById(Long.parseLong(username))
            .map(m -> new MyBeautipUserDetails(m))
            .orElseThrow(() -> new AuthenticationException("username not found"));
      }
    }
  }

  private MyBeautipUserDetails createGuestUserDetails() {
    return new MyBeautipUserDetails("guest", "ROLE_GUEST");
  }
}
