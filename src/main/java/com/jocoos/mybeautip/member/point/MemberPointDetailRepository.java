package com.jocoos.mybeautip.member.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberPointDetailRepository extends JpaRepository<MemberPointDetail, Long> {

    Optional<MemberPointDetail> findTopByMemberIdAndStateInAndExpiryAtAfterOrderByIdDesc(Long memberId, List<Integer> states, Date now);

    @Query("select min(mpd.id) as id, sum(mpd.point) as pointSum, max(mpd.expiryAt) as expiryAt from MemberPointDetail mpd where mpd.parentId = ?1 group by mpd.parentId")
    Optional<MemberPointSum> getSumByParentId(Long parentId);

    List<MemberPointDetail> findByParentId(Long parentId);

    List<MemberPointDetail> findByParentIdInAndState(List<Long> parentIds, int state);

    List<MemberPointDetail> findByParentIdAndState(Long parentId, int state);

    List<MemberPointDetail> findByOrderIdAndStateAndExpiryAtAfter(Long orderId, int state, Date now);

    List<MemberPointDetail> findAllByMemberIdAndStateAndParentIdIsNullOrderByIdAsc(long memberId, int state);

    List<MemberPointDetail> findAllByMemberIdAndMemberPointIdGreaterThanEqualAndStateOrderById(long memberId, long cursor, int state);
}
