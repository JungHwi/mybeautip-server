package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.domain.point.valid.DateValidator;
import com.jocoos.mybeautip.domain.point.valid.PerDomainValidator;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.VIDEO_LIKE;
import static com.jocoos.mybeautip.domain.point.valid.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class VideoLikePointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;

    @Value("${mybeautip.activity-point.video-like-date-limit}")
    private int dateLimitNum;

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        Video videoLike = (Video) validObject.getDomain();
        return validLikeByMe(videoLike, validObject.getReceiveMember()) &&
                perDomainValidator.valid(VIDEO_LIKE, validObject.getDomainId(), validObject.getReceiveMember()) &&
                dateValidator.valid(VIDEO_LIKE, day(this.dateLimitNum), validObject.getReceiveMember());
    }

    private boolean validLikeByMe(Video video, Member receiveMember) {
        return !Objects.equals(video.getMember().getId(), receiveMember.getId());
    }
}
