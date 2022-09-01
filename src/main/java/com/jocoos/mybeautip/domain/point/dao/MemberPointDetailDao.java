package com.jocoos.mybeautip.domain.point.dao;

import com.jocoos.mybeautip.member.point.MemberPointDetail;
import com.jocoos.mybeautip.member.point.MemberPointDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;

@RequiredArgsConstructor
@Service
public class MemberPointDetailDao {

    private final MemberPointDetailRepository repository;

    @Transactional
    public MemberPointDetail save(MemberPointDetail memberPointDetail) {
        return repository.save(memberPointDetail);
    }

    @Transactional
    public List<MemberPointDetail> saveAll(List<MemberPointDetail> memberPointDetails) {
        return repository.saveAll(memberPointDetails);
    }

    @Transactional(readOnly = true)
    public List<MemberPointDetail> findByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }

    @Transactional(readOnly = true)
    public List<MemberPointDetail> findUnderZeroPoints(Long memberId) {
        return repository.findAllByMemberIdAndStateAndParentIdIsNullOrderByIdAsc(memberId, STATE_UNDER_ZERO_POINT);
    }

    @Transactional(readOnly = true)
    public List<MemberPointDetail> findUsedPointOfParent(Long cursor) {
        return repository.findByParentIdAndState(cursor, STATE_USE_POINT);
    }

    @Transactional(readOnly = true)
    public MemberPointDetail findLastUsedOrUnderZeroPoint(Long memberId) {
        return repository.findTopByMemberIdAndStateInAndExpiryAtAfterOrderByIdDesc(
                        memberId,
                        Arrays.asList(STATE_UNDER_ZERO_POINT, STATE_USE_POINT),
                        Date.from(Instant.now()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MemberPointDetail> findAvailablePointsAfterCursor(Long memberId, Long cursor) {
        return repository.findAllByMemberIdAndMemberPointIdGreaterThanEqualAndStateOrderById(memberId, cursor, STATE_EARNED_POINT);
    }
}
