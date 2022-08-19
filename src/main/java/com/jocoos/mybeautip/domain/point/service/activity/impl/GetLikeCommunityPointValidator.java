package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_COMMUNITY;

@RequiredArgsConstructor
@Component
public class GetLikeCommunityPointValidator implements ActivityPointValidator {

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        CommunityLike like = (CommunityLike) validObject.getDomain();
        return validLikeByMe(like, validObject.getReceiveMember()) &&
                perDomainValidator.valid(GET_LIKE_COMMUNITY, like.getId(), validObject.getReceiveMember());
    }

    private boolean validLikeByMe(CommunityLike like, Member receiveMember) {
        return like.getMemberId() != receiveMember.getId();
    }
}
