package com.jocoos.mybeautip.domain.point.service.activity;

import com.jocoos.mybeautip.domain.point.util.ValidObject;

@FunctionalInterface
public interface ActivityPointValidator {
    boolean valid(ValidObject validObject);
}
