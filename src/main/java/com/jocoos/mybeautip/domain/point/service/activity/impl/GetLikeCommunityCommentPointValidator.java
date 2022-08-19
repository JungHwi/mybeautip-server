package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_COMMUNITY;

@Slf4j
@RequiredArgsConstructor
@Component
public class GetLikeCommunityCommentPointValidator implements ActivityPointValidator {
    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        CommunityCommentLike like = (CommunityCommentLike) validObject.getDomain();
        return validLikeByMe(like, validObject.getReceiveMember()) &&
                perDomainValidator.valid(GET_LIKE_COMMUNITY, like.getId(), validObject.getReceiveMember());
    }

    private boolean validLikeByMe(CommunityCommentLike like, Member receiveMember) {
        log.info("{}, {}", like.getMemberId(), receiveMember.getId());
        return like.getMemberId() != receiveMember.getId();
    }
}
