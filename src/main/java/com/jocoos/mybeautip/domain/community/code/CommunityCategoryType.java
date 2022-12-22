package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.impl.*;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jocoos.mybeautip.global.exception.ErrorCode.CATEGORY_NO_WRITABLE;

@AllArgsConstructor
@Getter
public enum CommunityCategoryType implements CodeValue {

    GENERAL("일반 게시판", null),
    ANONYMOUS("익명 게시판", null),
    GROUP("그룹", null),
    NORMAL("일반", new NormalCommunityValidator()),
    BLIND("속닥속닥", new BlindCommunityValidator()),
    DRIP("써봐줄게", new DripCommunityValidator()),
    EVENT("이벤트", null),
    VOTE("결정픽", new VoteCommunityValidator()),
    KING_TIP("인슷하", new KingTipCommunityValidator());

    private final String description;
    private final CommunityValidator validator;

    private static final Set<CommunityCategoryType> summaryTypes = new HashSet<>(Arrays.asList(BLIND, VOTE));
    public static final Set<CommunityCategoryType> NOT_IN_ADMIN = new HashSet<>(Arrays.asList(GROUP, EVENT));

    public static boolean supportsSummary(CommunityCategoryType type) {
        return summaryTypes.contains(type);
    }

    @Override
    public String getName() {
        return this.name();
    }

    public void validWrite(Community community) {
        if (validator == null) {
            throw new BadRequestException(CATEGORY_NO_WRITABLE);
        }
        validator.validWriteAuth(community.getMemberRole());
        validator.validCommunity(community);
    }

    public void validContent(Role role, String contents) {
        if (validator == null) {
            throw new BadRequestException(CATEGORY_NO_WRITABLE);
        }
        validator.validContentsByRole(role, contents);
    }

    public void validReadAuth(Role role) {
        if (validator == null) {
            return;
        }
        validator.validReadAuth(role);
    }
}
