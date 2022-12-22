package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityValidatorFactory {

    public static CommunityValidator getValidator(CommunityCategoryType type) {
        return switch (type) {
            case DRIP -> new DripCommunityValidator();
            case VOTE -> new VoteCommunityValidator();
            case KING_TIP -> new KingTipCommunityValidator();
            case BLIND -> new BlindCommunityValidator();
            default -> new NormalCommunityValidator();
        };
    }
}
