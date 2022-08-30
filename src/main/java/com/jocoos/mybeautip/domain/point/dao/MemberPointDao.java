package com.jocoos.mybeautip.domain.point.dao;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.code.PointStatusGroup;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT;

@RequiredArgsConstructor
@Service
public class MemberPointDao {

    private final MemberPointRepository repository;

    public Long countActivityPointDaily(ActivityPointType type, Member member) {
        return repository.countActivityPointDailyByType(type, member);
    }

    public Long countActivityPointDaily(Set<ActivityPointType> types, Member member) {
        return repository.countActivityPointDailyByTypes(types, member);
    }

    public boolean isExistByTypeAndMember(ActivityPointType type, Member member) {
        return repository.existsByActivityTypeAndMemberAndState(type, member, STATE_EARNED_POINT);
    }

    public boolean isExistByTypeAndDomainIdAndMember(ActivityPointType type, Long domainId, Member member) {
        return repository.existsByActivityTypeAndActivityDomainIdAndMemberAndState(type, domainId, member, STATE_EARNED_POINT);
    }

    public List<MemberPoint> getAvailablePoint(Long memberId, Long cursor) {
        return repository.getAvailablePoint(memberId, PointStatusGroup.EARN.getLegacyCodeGroup(), cursor);
    }
}
