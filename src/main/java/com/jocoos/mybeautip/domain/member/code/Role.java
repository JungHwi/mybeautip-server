package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Role implements CodeValue {
    GUEST("게스트", "ROLE_GUEST"),
    USER("유저", "ROLE_USER"),
    ADMIN("어드민", "ROLE_ADMIN");

    private final String description;
    private final String authority;

    public static final Set<Role> ALL_ROLES = Set.of(values());

    public static Role from(Member member) {
        if (member == null) {
            return GUEST;
        }
        return member.getRole();
    }

    public static Role from(Integer link) {
        if (link == null) {
            return GUEST;
        }
        if (link == 0) {
            return ADMIN;
        }
        return USER;
    }

    @Override
    public String getName() {
        return name();
    }
}
