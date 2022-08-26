package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.domain.point.valid.PerDomainValidator;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_COMMUNITY_COMMENT;

@RequiredArgsConstructor
@Component
public class GetLikeCommunityCommentPointValidator implements ActivityPointValidator {
    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        CommunityCommentLike like = (CommunityCommentLike) validObject.getDomain();
        return validLikeByMe(like, validObject.getReceiveMember()) &&
                perDomainValidator.valid(GET_LIKE_COMMUNITY_COMMENT, like.getId(), validObject.getReceiveMember());
    }

    private boolean validLikeByMe(CommunityCommentLike like, Member receiveMember) {
        return like.getMemberId() != receiveMember.getId();
    }
}
