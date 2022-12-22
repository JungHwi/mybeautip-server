package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunityAuthority;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import static com.jocoos.mybeautip.domain.member.code.Role.ADMIN;
import static com.jocoos.mybeautip.domain.member.code.Role.USER;

public class BlindCommunityValidator extends CommunityCommonValidator {

    private final CommunityAuthority authority = new CommunityAuthority(Set.of(USER, ADMIN), Set.of(USER));

    @Override
    public CommunityAuthority getCommunityAuth() {
        return authority;
    }

    @Override
    public void validCommunity(Community community) {
        super.validByRole(community.getMemberRole(), community.getContents(), community.getFileSize());
        validBlind(community.getTitle());
    }

    private void validBlind(String title) {
        if (StringUtils.isBlank(title) || title.length() < 5) {
            throw new BadRequestException("Community title of Blind Category must be over 5 length");
        }
    }
}
