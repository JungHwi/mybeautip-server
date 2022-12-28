package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;

import java.util.Set;

import static com.jocoos.mybeautip.domain.member.code.Role.ADMIN;
import static com.jocoos.mybeautip.domain.member.code.Role.ALL_ROLES;

public class KingTipCommunityValidator extends CommunityCommonValidator {

    private static final int KING_TIP_FILE_SIZE_LIMIT = 10;
    private final CommunityAuthority authority = new CommunityAuthority(ALL_ROLES, Set.of(ADMIN));

    @Override
    public CommunityAuthority getCommunityAuth() {
        return authority;
    }

    @Override
    public void validCommunity(Community community) {
        super.validFileCount(community.getFileSize(), KING_TIP_FILE_SIZE_LIMIT);
    }
}
