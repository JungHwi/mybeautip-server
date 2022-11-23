package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.member.code.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.jocoos.mybeautip.domain.member.code.Role.*;

@Getter
@RequiredArgsConstructor
public class CommunityAuthority {

    private final Set<Role> readAuth;
    private final Set<Role> writeAuth;

    public CommunityAuthority(Set<Role> requiredReadWriteRoles) {
        this.writeAuth = requiredReadWriteRoles;
        this.readAuth = requiredReadWriteRoles;
    }

    public static CommunityAuthority readAllRoleWriteUserAdmin() {
        return new CommunityAuthority(Set.of(USER, ADMIN), ALL_ROLES);
    }

    public static CommunityAuthority readAllRoleNoWriteRole() {
        return new CommunityAuthority(ALL_ROLES, Set.of());
    }

    public boolean canWrite(Role role) {
        return writeAuth.contains(role);
    }

    public boolean canRead(Role role) {
        return readAuth.contains(role);
    }
}
