package com.jocoos.mybeautip.domain.point.service.activity;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_PHOTO_POST;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_POST;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityPointService {

    private final MemberPointRepository memberPointRepository;

    private final MemberPointService memberPointService;

    @Transactional
    public void gainActivityPoint(ActivityPointType type, Long domainId,  Member member) {
        if (validDateLimit(type, member) && validPerDomain(type, domainId, member))
            memberPointService.earnPoint(type, domainId, member);
    }

    @Transactional
    public void retrieveActivityPoint(ActivityPointType type, Long domainId, Member member) {
        if (validPointIssued(type, domainId, member))
            memberPointService.retrievePoints(type, domainId, member);
    }

    @Transactional
    public void retrieveActivityPoint(List<ActivityPointType> types, Long domainId, Member member) {
        ActivityPointType type = findAndValidExactActivityType(types, domainId, member);
        if (!Objects.isNull(type))
            memberPointService.retrievePoints(type, domainId, member);
    }

    private boolean validDateLimit(ActivityPointType type, Member member) {
        switch (type) {
            case WRITE_POST: case WRITE_PHOTO_POST:
                return validDay(Arrays.asList(WRITE_PHOTO_POST, WRITE_POST), WRITE_POST.getDateLimitNum(), member);
            default:
                return validDateLimitExactType(type, member);
        }
    }

    private boolean validPerDomain(ActivityPointType type, Long domainId, Member member) {
        if (!type.isPerDomainRestrict()) return true;
        else return !validPointIssued(type, domainId, member);
    }

    private ActivityPointType findAndValidExactActivityType(List<ActivityPointType> types,
                                                            Long domainId,
                                                            Member member) {
        return memberPointRepository
                .findExactTypeByTypesAndDomainIdAndMember(types, domainId, member).orElse(null);
    }

    private boolean validDateLimitExactType(ActivityPointType type, Member member) {
        switch (type.getDateLimit()) {
            case ALL_TIME_ONCE:
                return validAllTimeOnce(type, member);
            case DAY:
                return validDay(type, member);
            default:
                return true;
        }
    }

    private boolean validPointIssued(ActivityPointType type, Long domainId, Member member) {
        return memberPointRepository
                .existsByActivityTypeAndActivityDomainIdAndMemberAndState(type, domainId, member, STATE_EARNED_POINT);
    }

    private boolean validDay(ActivityPointType type, Member member) {
        return memberPointRepository.isActivityTodayLessThanLimit(type, type.getDateLimitNum(), member);
    }

    private boolean validDay(List<ActivityPointType> types, long limitNum, Member member) {
        return memberPointRepository.isActivityTodayLessThanLimit(types, limitNum, member);
    }

    private boolean validAllTimeOnce(ActivityPointType type, Member member) {
        return !memberPointRepository.existsByActivityTypeAndMemberAndState(type, member, STATE_EARNED_POINT);
    }
}
