package com.jocoos.mybeautip.domain.point.util;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_COMMUNITY_TYPES;

@RequiredArgsConstructor
@Component
public class DateValidator {

    private final MemberPointDao memberPointDao;

    public boolean valid(ActivityPointType type, DateRestriction restriction, Member member) {
        switch (type) {
            case WRITE_COMMUNITY:
            case WRITE_PHOTO_COMMUNITY:
                return validDay(WRITE_COMMUNITY_TYPES, restriction.getLimitNum(), member);
            default:
                return validDateLimit(type, restriction, member);
        }
    }

    public boolean valid(Set<ActivityPointType> types, DateRestriction restriction, Member member) {
        if (WRITE_COMMUNITY_TYPES.equals(types)) {
            return validDay(WRITE_COMMUNITY_TYPES, restriction.getLimitNum(), member);
        } return false;
    }

    private boolean validDateLimit(ActivityPointType type, DateRestriction restriction, Member member) {
        switch (restriction.getDateLimit()) {
            case ALL_TIME_ONCE:
                return validAllTimeOnce(type, member);
            case DAY:
                return validDay(type, restriction.getLimitNum(), member);
            default:
                return true;
        }
    }

    private boolean validDay(ActivityPointType type, Long limitNum, Member member) {
        return memberPointDao.countActivityPointDaily(type, member) < limitNum;
    }

    private boolean validDay(Set<ActivityPointType> types, Long limitNum, Member member) {
        return memberPointDao.countActivityPointDaily(types, member) < limitNum;
    }

    private boolean validAllTimeOnce(ActivityPointType type, Member member) {
        return !memberPointDao.isExistByTypeAndMember(type, member);
    }
}
