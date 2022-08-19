package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.DateValidator;
import com.jocoos.mybeautip.domain.point.util.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.VIDEO_LIKE;
import static com.jocoos.mybeautip.domain.point.util.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class VideoLikePointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;
    private static final int DATE_LIMIT_NUM = 5;

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        return perDomainValidator.valid(VIDEO_LIKE, validObject.getDomainId(), validObject.getReceiveMember()) &&
                dateValidator.valid(VIDEO_LIKE, day(DATE_LIMIT_NUM), validObject.getReceiveMember());
    }
}
