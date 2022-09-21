package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDao;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointFactory;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.service.activity.MultiTypeActivityPointValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.CANCEL_VIDEO_LIKE;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.CANCEL_VIDEO_SCRAP;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_RETRIEVE_POINT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityPointService {

    private final MemberPointService memberPointService;
    private final ActivityPointFactory activityPointFactory;

    private final MemberPointDao memberPointDao;

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
        if (isOneTimeRetrieveType(type) && isRetrievedBefore(type, domainId, member)) {
            return;
        }
        memberPointService.retrievePoints(type, domainId, member);
    }

    private boolean isRetrievedBefore(ActivityPointType type, Long domainId, Member member) {
        return memberPointDao.isExistByTypeAndDomainIdAndMemberAndState(type, domainId, member, STATE_RETRIEVE_POINT);
    }

    private boolean isOneTimeRetrieveType(ActivityPointType type) {
        return CANCEL_VIDEO_LIKE.equals(type) || CANCEL_VIDEO_SCRAP.equals(type);
    }

    @Transactional
    public void retrieveActivityPoint(Set<ActivityPointType> types, ValidObject validObject) {
        MultiTypeActivityPointValidator validator = activityPointFactory.getValidator(types);
        ActivityPointType type = validator.getRetrieveType(validObject);
        retrieveActivityPoint(type, validObject.getDomainId(), validObject.getReceiveMember());
    }
}
