package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.PerDomainValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_VIDEO;

@RequiredArgsConstructor
@Component
public class GetLikeVideoPointValidator implements ActivityPointValidator {

    private final PerDomainValidator perDomainValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        return perDomainValidator.valid(GET_LIKE_VIDEO, validObject.getDomainId(), validObject.getMember());
    }
}
