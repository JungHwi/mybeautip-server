package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.member.code.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.jocoos.mybeautip.domain.member.code.Role.*;

@Getter
@RequiredArgsConstructor
public class CommunityAuthority {

    private final Set<Role> writeAuth;
    private final Set<Role> readAuth;

    public CommunityAuthority(Set<Role> auth) {
        this.writeAuth = auth;
        this.readAuth = auth;
    }

    public CommunityAuthority(Role role, Set<Role> roles) {
        this.writeAuth = Set.of(role);
        this.readAuth = roles;
    }

    public static CommunityAuthority defaultAuth() {
        return new CommunityAuthority(Set.of(USER, ADMIN), ALL);
    }

    public boolean canWrite(Role role) {
        return writeAuth.contains(role);
    }

    public boolean canRead(Role role) {
        return readAuth.contains(role);
    }
}
