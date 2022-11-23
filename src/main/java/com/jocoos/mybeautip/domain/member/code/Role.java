package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    public static final Set<Role> ALL_ROLES = Set.of(values());

    public static Role from(Member member) {
        if (member == null) {
            return GUEST;
        }
        return member.getRole();
    }
}
