package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.service.activity.MultiTypeActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.DateValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.*;
import static com.jocoos.mybeautip.domain.point.util.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class WriteCommunityTypesPointValidator implements MultiTypeActivityPointValidator {

    private final DateValidator dateValidator;
    private static final int DATE_LIMIT_NUM = 5;
    private static final int CONTENT_MIN_NUM = 30;

    @Override
    public boolean valid(ValidObject validObject) {
        return dateValidator.valid(WRITE_COMMUNITY_TYPES, day(DATE_LIMIT_NUM), validObject.getMember());
    }

    @Override
    public Optional<ActivityPointType> getType(ValidObject validObject) {
        Community community = (Community) validObject.getDomain();
        return validContentLength(community);
    }

    @Override
    public ActivityPointType getRetrieveType(ValidObject validObject) {
        Community community = (Community) validObject.getDomain();
        if (community.isImageExist()) {
            return WRITE_PHOTO_COMMUNITY;
        } else {
            return WRITE_COMMUNITY;
        }
    }

    private Optional<ActivityPointType> validContentLength(Community community) {
        if (community.isContentLongerThanOrSame(CONTENT_MIN_NUM)) {
            return isPhotoCommunityOrDefault(community);
        } else {
            return Optional.empty();
        }
    }

    private Optional<ActivityPointType> isPhotoCommunityOrDefault(Community community) {
        if (community.isImageExist()) {
            return Optional.of(WRITE_PHOTO_COMMUNITY);
        } else {
            return Optional.of(WRITE_COMMUNITY);
        }
    }
}
