package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.exception.BadRequestException;

import java.util.Set;

import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;

public interface CommunityValidator {

    CommunityAuthority getCommunityAuth();
    void validCommunity(Community community);
    void validContentsByRole(Role role, String content);

    default void validReadAuth(Role role) {
        CommunityAuthority authority = getCommunityAuth();
        validReadAuth(authority, role);
    }

    default void validWriteAuth(Role role) {
        CommunityAuthority authority = getCommunityAuth();
        validWriteAuth(authority, role);
    }

    private void validReadAuth(CommunityAuthority authority, Role role) {
        if (!authority.canRead(role)) {
            throw new BadRequestException(ACCESS_DENIED, authErrorMessage(role, authority.getReadAuth()));
        }
    }

    private void validWriteAuth(CommunityAuthority authority, Role role) {
        if (!authority.canWrite(role)) {
            throw new BadRequestException(ACCESS_DENIED, authErrorMessage(role, authority.getWriteAuth()));
        }
    }

    private String authErrorMessage(Role role, Set<Role> requiredRoles) {
        return "need role " + requiredRoles + ", request role is " + role;
    }
}
