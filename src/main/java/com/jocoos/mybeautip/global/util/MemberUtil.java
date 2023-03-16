package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.GUEST_TOKEN_PREFIX;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MemberUtil {

    public static boolean isGuest(String username) {
        return StringUtils.hasText(username) && username.startsWith(GUEST_TOKEN_PREFIX);
    }

    public static Long getGuestId(@NotNull String username) {
        return Long.parseLong(getGuestUsernameWithoutPrefix(username));
    }

    public static String getGuestUsernameWithoutPrefix(String username) {
        return username.replace(GUEST_TOKEN_PREFIX, "");
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
