package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.VideoLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_VIDEO;

@RequiredArgsConstructor
@Component
public class GetLikeVideoPointValidator implements ActivityPointValidator {

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        VideoLike like = (VideoLike) validObject.getDomain();
        return validLikeByMe(like, validObject.getReceiveMember()) &&
                perDomainValidator.valid(GET_LIKE_VIDEO, like.getId(), validObject.getReceiveMember());
    }

    private boolean validLikeByMe(VideoLike like, Member receiveMember) {
        return !like.getCreatedBy().equals(receiveMember);
    }
}
