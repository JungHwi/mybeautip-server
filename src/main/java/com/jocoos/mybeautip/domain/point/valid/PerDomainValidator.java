package com.jocoos.mybeautip.domain.point.valid;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT;

@RequiredArgsConstructor
@Component
public class PerDomainValidator {

    private final MemberPointDao memberPointDao;

    public boolean valid(ActivityPointType type, Long domainId, Member member) {
        return !memberPointDao.isExistByTypeAndDomainIdAndMemberAndState(type, domainId, member, STATE_EARNED_POINT);
    }
}
