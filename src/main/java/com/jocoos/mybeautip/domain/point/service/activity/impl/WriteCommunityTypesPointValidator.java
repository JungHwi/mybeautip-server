package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.service.activity.MultiTypeActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.DateValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.*;
import static com.jocoos.mybeautip.domain.point.valid.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class WriteCommunityTypesPointValidator implements MultiTypeActivityPointValidator {

    private final DateValidator dateValidator;

    @Value("${mybeautip.activity-point.community-write-date-limit}")
    private int dateLimitNum;

    @Value("${mybeautip.activity-point.community-valid-length}")
    private int contentMinNum;

    @Override
    public boolean valid(ValidObject validObject) {
        return dateValidator.valid(WRITE_COMMUNITY_TYPES, day(this.dateLimitNum), validObject.getReceiveMember());
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
            return DELETE_PHOTO_COMMUNITY;
        } else {
            return DELETE_COMMUNITY;
        }
    }

    private Optional<ActivityPointType> validContentLength(Community community) {
        if (community.isContentLongerThanOrSame(this.contentMinNum)) {
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
