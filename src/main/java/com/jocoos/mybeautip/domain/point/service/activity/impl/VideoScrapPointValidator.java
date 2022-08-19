package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.DateValidator;
import com.jocoos.mybeautip.domain.point.valid.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.VIDEO_SCRAP;
import static com.jocoos.mybeautip.domain.point.valid.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class VideoScrapPointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;

    @Value("${mybeautip.activity-point.video-scrap-date-limit}")
    private int dateLimitNum;

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        return perDomainValidator.valid(VIDEO_SCRAP, validObject.getDomainId(), validObject.getReceiveMember()) &&
                dateValidator.valid(VIDEO_SCRAP, day(this.dateLimitNum), validObject.getReceiveMember());
    }
}
