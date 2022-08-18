package com.jocoos.mybeautip.domain.point.service.activity;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.util.ValidObject;

import java.util.Optional;

public interface MultiTypeActivityPointValidator extends ActivityPointValidator {
    Optional<ActivityPointType> getType(ValidObject validObject);
    ActivityPointType getRetrieveType(ValidObject validObject);
}
