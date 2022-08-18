package com.jocoos.mybeautip.domain.point.util;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PerDomainValidator {

    private final MemberPointDao memberPointDao;

    public boolean valid(ActivityPointType type, Long domainId, Member member) {
        return memberPointDao.isExistByTypeAndDomainIdAndMember(type, domainId, member);
    }
}
