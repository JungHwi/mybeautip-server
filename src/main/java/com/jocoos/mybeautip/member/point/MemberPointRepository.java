package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {

    List<MemberPoint> findByIdIn(List<Long> ids);

    Slice<MemberPoint> findByMemberAndCreatedAtBefore(Member member, Date createdAt, Pageable page);

    List<MemberPoint> findByMemberAndState(Member member, int state);

    List<MemberPoint> findByStateAndCreatedAtBeforeAndEarnedAtIsNull(int state, Date createdAt);

    List<MemberPoint> findByStateInAndExpiryAtBeforeAndExpiredAtIsNull(List<Integer> states, Date earnedAt);

    List<MemberPoint> findByStateInAndExpiryAtBeforeAndRemindIsFalseAndExpiredAtIsNull(List<Integer> states, Date earnedAt);

    Page<MemberPoint> findByMemberId(Long memberId, Pageable pageable);

    Page<MemberPoint> findByMemberIdAndState(Long memberId, int state, Pageable pageable);

    Optional<MemberPoint> findByMemberAndOrderAndPointAndState(Member member, Order order, int point, int state);

    @Query("SELECT point " +
            "FROM MemberPoint as point " +
            "WHERE point.state = 1 " +
            "   AND point.member.id = ?1 " +
            "   AND point.id >= ?2 " +
            "ORDER BY point.id")
    List<MemberPoint> getAvailablePoint(long memberId, Long id);


    // admin api
    Page<MemberPoint> findByStateAndOrderIsNull(int state, Pageable pageable);


}
