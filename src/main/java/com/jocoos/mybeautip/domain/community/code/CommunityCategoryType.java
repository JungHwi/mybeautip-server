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

import static com.jocoos.mybeautip.domain.community.vo.CommunityAuthority.defaultAuth;
import static com.jocoos.mybeautip.domain.member.code.Role.*;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;

@AllArgsConstructor
@Getter
public enum CommunityCategoryType implements CodeValue {

    GENERAL("일반 게시판", defaultAuth()),
    ANONYMOUS("익명 게시판", defaultAuth()),
    GROUP("그룹", defaultAuth()),
    NORMAL("일반", defaultAuth()),
    BLIND("속닥속닥", new CommunityAuthority(Set.of(USER, ADMIN))),
    DRIP("드립", defaultAuth()),
    EVENT("이벤트", defaultAuth()),
    VOTE("결정픽", defaultAuth()),
    KING_TIP("마왕팁", new CommunityAuthority(ADMIN, ALL));

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
            throw new BadRequestException(ACCESS_DENIED, writeAuthErrorMessage(role));
        }
    }

    private String writeAuthErrorMessage(Role role) {
        return "category " + name() + " need role " + authority.getWriteAuth() + ", request role is " + role;
    }
}
