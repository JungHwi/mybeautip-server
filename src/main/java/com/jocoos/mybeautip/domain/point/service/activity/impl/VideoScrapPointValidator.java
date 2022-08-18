package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.DateValidator;
import com.jocoos.mybeautip.domain.point.util.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.VIDEO_SCRAP;
import static com.jocoos.mybeautip.domain.point.util.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class VideoScrapPointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;
    private static final int DATE_LIMIT_NUM = 2;

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        return perDomainValidator.valid(VIDEO_SCRAP, validObject.getDomainId(), validObject.getMember()) &&
                dateValidator.valid(VIDEO_SCRAP, day(DATE_LIMIT_NUM), validObject.getMember());
    }
}
