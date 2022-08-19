package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.CommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_VIDEO_COMMENT;

@RequiredArgsConstructor
@Component
public class GetLikeVideoCommentPointValidator implements ActivityPointValidator {

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        CommentLike like = (CommentLike) validObject.getDomain();
        return validLikeByMe(like, validObject.getReceiveMember()) &&
                perDomainValidator.valid(GET_LIKE_VIDEO_COMMENT, like.getId(), validObject.getReceiveMember());
    }

    private boolean validLikeByMe(CommentLike like, Member receiveMember) {
        return !like.getCreatedBy().equals(receiveMember);
    }
}
