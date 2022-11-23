package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jocoos.mybeautip.domain.community.vo.CommunityAuthority.readAllRoleNoWriteRole;
import static com.jocoos.mybeautip.domain.community.vo.CommunityAuthority.readAllRoleWriteUserAdmin;
import static com.jocoos.mybeautip.domain.member.code.Role.*;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;

@AllArgsConstructor
@Getter
public enum CommunityCategoryType implements CodeValue {

    GENERAL("일반 게시판", readAllRoleNoWriteRole()),
    ANONYMOUS("익명 게시판", readAllRoleNoWriteRole()),
    GROUP("그룹", readAllRoleNoWriteRole()),
    NORMAL("일반", readAllRoleWriteUserAdmin()),
    BLIND("속닥속닥", new CommunityAuthority(Set.of(USER, ADMIN))),
    DRIP("드립", readAllRoleWriteUserAdmin()),
    EVENT("이벤트", readAllRoleNoWriteRole()),
    VOTE("결정픽", readAllRoleWriteUserAdmin()),
    KING_TIP("마왕팁", new CommunityAuthority(ALL_ROLES, Set.of(ADMIN)));

    private final String description;
    private final CommunityAuthority authority;

    private static final Set<CommunityCategoryType> summaryTypes = new HashSet<>(Arrays.asList(BLIND, VOTE));
    public static final Set<CommunityCategoryType> NOT_IN_ADMIN = new HashSet<>(Arrays.asList(GROUP, EVENT));

    public static boolean supportsSummary(CommunityCategoryType type) {
        return summaryTypes.contains(type);
    }

    @Override
    public String getName() {
        return this.name();
    }

    public void validWriteAuth(Role role) {
        if (!authority.canWrite(role)) {
            throw new BadRequestException(ACCESS_DENIED, "write " + authErrorMessage(role, authority.getWriteAuth()));
        }
    }

    public void validReadAuth(Role role) {
        if (!authority.canRead(role)) {
            throw new BadRequestException(ACCESS_DENIED, "read " + authErrorMessage(role, authority.getReadAuth()));
        }
    }

    private String authErrorMessage(Role role, Set<Role> requiredRoles) {
        return "category " + name() + " need role " + requiredRoles + ", request role is " + role;
    }
}
