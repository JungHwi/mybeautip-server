package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MemberUtil {

    private static final String GUEST_PREFIX = "guest:";

    public static boolean isGuest(String username) {
        return StringUtils.hasText(username) && username.startsWith(GUEST_PREFIX);
    }

    public static Member getCurrentMember() {
        if (getPrincipal() instanceof MyBeautipUserDetails userDetails) {
            return userDetails.getMember();
        }
        return null;
    }

    private static Object getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
