package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointFactory;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.service.activity.MultiTypeActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityPointService {

    private final MemberPointService memberPointService;
    private final ActivityPointFactory activityPointFactory;

    @Transactional
    public void gainActivityPoint(ActivityPointType type, ValidObject validObject) {
        ActivityPointValidator validator = activityPointFactory.getValidator(type);
        if (validator.valid(validObject)) {
            memberPointService.earnPoint(type, validObject.getDomainId(), validObject.getReceiveMember());
        }
    }

    @Transactional
    public void gainActivityPoint(Set<ActivityPointType> types, ValidObject validObject) {
        MultiTypeActivityPointValidator validator = activityPointFactory.getValidator(types);
        Optional<ActivityPointType> typeOptional = validator.getType(validObject);
        typeOptional.ifPresent(type -> gainActivityPoint(type, validObject));
    }

    @Transactional
    public void retrieveActivityPoint(ActivityPointType type, Long domainId, Member member) {
        memberPointService.retrievePoints(type, domainId, member);
    }

    @Transactional
    public void retrieveActivityPoint(Set<ActivityPointType> types, ValidObject validObject) {
        MultiTypeActivityPointValidator validator = activityPointFactory.getValidator(types);
        ActivityPointType type = validator.getRetrieveType(validObject);
        retrieveActivityPoint(type, validObject.getDomainId(), validObject.getReceiveMember());
    }
}
