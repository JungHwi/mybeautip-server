package com.jocoos.mybeautip.domain.point.service.activity.impl;


import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.DateValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.INPUT_EXTRA_INFO;
import static com.jocoos.mybeautip.domain.point.valid.DateRestriction.allTimeOnce;

@RequiredArgsConstructor
@Component
public class InputExtraInfoPointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;

    @Override
    public boolean valid(ValidObject validObject) {
        return dateValidator.valid(INPUT_EXTRA_INFO, allTimeOnce(), validObject.getReceiveMember());
    }
}
