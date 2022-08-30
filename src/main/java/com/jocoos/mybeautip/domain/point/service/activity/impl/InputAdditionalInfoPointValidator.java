package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.DateValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.INPUT_ADDITIONAL_INFO;
import static com.jocoos.mybeautip.domain.point.valid.DateRestriction.allTimeOnce;

@RequiredArgsConstructor
@Component
public class InputAdditionalInfoPointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        return dateValidator.valid(INPUT_ADDITIONAL_INFO, allTimeOnce(), validObject.getReceiveMember());
    }
}
