package com.jocoos.mybeautip.domain.point.dao;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT;

@RequiredArgsConstructor
@Service
public class MemberPointDao {

    private final MemberPointRepository memberPointRepository;

    public Long countActivityPointDaily(ActivityPointType type, Member member) {
        return memberPointRepository.countActivityPointDailyByType(type, member);
    }

    public Long countActivityPointDaily(Set<ActivityPointType> types, Member member) {
        return memberPointRepository.countActivityPointDailyByTypes(types, member);
    }

    public boolean isExistByTypeAndMember(ActivityPointType type, Member member) {
        return memberPointRepository.existsByActivityTypeAndMemberAndState(type, member, STATE_EARNED_POINT);
    }

    public boolean isExistByTypeAndDomainIdAndMember(ActivityPointType type, Long domainId, Member member) {
        return memberPointRepository
                .existsByActivityTypeAndActivityDomainIdAndMemberAndState(type, domainId, member, STATE_EARNED_POINT);
    }
}
