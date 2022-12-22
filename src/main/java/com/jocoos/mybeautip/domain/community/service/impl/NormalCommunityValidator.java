package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;

public class NormalCommunityValidator extends CommunityCommonValidator {

    private final CommunityAuthority authority = CommunityAuthority.readAllRoleWriteUserAdmin();

    @Override
    public CommunityAuthority getCommunityAuth() {
        return authority;
    }

    @Override
    public void validCommunity(Community community) {
        super.validByRole(community.getMemberRole(), community.getContents(), community.getFileSize());
    }
}
