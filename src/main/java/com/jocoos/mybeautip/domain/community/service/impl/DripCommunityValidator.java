package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;
import com.jocoos.mybeautip.global.exception.BadRequestException;

import static com.jocoos.mybeautip.domain.community.vo.CommunityAuthority.readAllRoleWriteUserAdmin;

public class DripCommunityValidator extends CommunityCommonValidator {

    private final CommunityAuthority authority = readAllRoleWriteUserAdmin();

    @Override
    public CommunityAuthority getCommunityAuth() {
        return authority;
    }

    @Override
    public void validCommunity(Community community) {
        super.validByRole(community.getMemberRole(), community.getContents(), community.getFileSize());
        validDrip(community.getEventId());
    }

    private void validDrip(Long eventId) {
        if (eventId == null || eventId < 1) {
            throw new BadRequestException("Community of drip category needs event_id.");
        }
    }
}
