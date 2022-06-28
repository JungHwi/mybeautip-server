package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MybeautipUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

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
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such member. id - " + username));
    }

    private MyBeautipUserDetails createGuestUserDetails(String username) {
        return new MyBeautipUserDetails(username, "ROLE_GUEST");
    }
}
