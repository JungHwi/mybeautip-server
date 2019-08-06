package com.jocoos.mybeautip.member.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberPointDetailRepository extends JpaRepository<MemberPointDetail, Long> {
    Optional<MemberPointDetail> findTopByMemberIdAndStateAndExpiryAtAfterOrderByIdDesc(Long memberId, int state, Date now);

    @Query("select min(mpd.id) as id, sum(mpd.point) as pointSum, max(mpd.expiryAt) as expiryAt from MemberPointDetail mpd where mpd.parentId = ?1 group by mpd.parentId")
    Optional<MemberPointSum> getSumByParentId(Long parentId);

    @Query("select sum(mpd.point) as pointSum, max(mpd.expiryAt) as expiryAt from MemberPointDetail mpd where mpd.memberPointId = ?1 group by mpd.memberPointId")
    Optional<MemberPointSum> getSumByMemberPointId(Long memberPointId);

    Optional<MemberPointDetail> findByMemberPointId(Long memberPointId);

    @Query("select mpd from MemberPointDetail mpd where mpd.memberId = ?1 and mpd.state = ?2 and mpd.id > ?3 and expiry_at >= now() order by mpd.createdAt asc")
    List<MemberPointDetail> getAllEarnedPoints(Long memberId, int state, Long cursor);

    List<MemberPointDetail> findByOrderIdAndStateAndExpiryAtAfter(Long orderId, int state, Date now);
}
