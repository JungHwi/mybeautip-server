package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;
import com.jocoos.mybeautip.global.exception.BadRequestException;

import static com.jocoos.mybeautip.global.exception.ErrorCode.NOT_SUPPORTED_VOTE_NUM;

public class VoteCommunityValidator extends CommunityCommonValidator {

    private final CommunityAuthority authority = CommunityAuthority.readAllRoleWriteUserAdmin();

    @Override
    public CommunityAuthority getCommunityAuth() {
        return authority;
    }

    @Override
    public void validCommunity(Community community) {
        super.validByRole(community.getMemberRole(), community.getContents(), community.getFileSize());
        validVote(community.getVoteSize());
    }

    private void validVote(int voteSize) {
        if (voteSize != 0 && voteSize != 2) {
            throw new BadRequestException(NOT_SUPPORTED_VOTE_NUM.getDescription());
        }
    }
}
