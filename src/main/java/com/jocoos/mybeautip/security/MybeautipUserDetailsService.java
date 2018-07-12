package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.member.Member;
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
        return new MybeautipGuestUserDetails();
      }
      default: {
        return memberRepository.findById(Long.parseLong(username))
            .map(m -> new MybeautipUserDetails(m))
            .orElseThrow(() -> new UsernameNotFoundException("username not found"));
      }
    }
  }

  class MybeautipUserDetails extends User {
    public MybeautipUserDetails(Member member) {
      super(member.getId().toString(), "", AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
  }

  class MybeautipGuestUserDetails extends User {
    public MybeautipGuestUserDetails() {
      super("guest", "", AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
  }
}
