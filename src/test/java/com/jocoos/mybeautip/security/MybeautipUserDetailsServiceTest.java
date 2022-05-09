package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.member.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class MybeautipUserDetailsServiceTest implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = new Member();
        member.setId(4L);
        member.setUsername("Breeze");
        return new MyBeautipUserDetails(member);
    }
}