package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {

    List<MemberPoint> findByIdIn(List<Long> ids);

    Slice<MemberPoint> findByMemberAndCreatedAtBefore(Member member, Date createdAt, Pageable page);

    List<MemberPoint> findByMemberAndState(Member member, int state);

    List<MemberPoint> findByStateAndCreatedAtBeforeAndEarnedAtIsNull(int state, Date createdAt);

    List<MemberPoint> findByStateInAndExpiryAtBeforeAndExpiredAtIsNull(List<Integer> states, Date earnedAt);

    List<MemberPoint> findByStateInAndExpiryAtBeforeAndRemindIsFalseAndExpiredAtIsNull(List<Integer> states, Date earnedAt);

    @Query("SELECT memberPoint " +
            "FROM MemberPoint AS memberPoint " +
            "WHERE memberPoint.member.id = :memberId " +
            "   AND memberPoint.id < :cursor ")
    Slice<MemberPoint> findByMemberId(Long memberId, Long cursor, Pageable pageable);

    @Query("SELECT memberPoint " +
            "FROM MemberPoint AS memberPoint " +
            "WHERE memberPoint.member.id = :memberId " +
            "   AND memberPoint.id < :cursor " +
            "   AND memberPoint.state in :state")
    Slice<MemberPoint> findByMemberIdAndStateIn(long memberId, Set<Integer> state, Long cursor, Pageable pageable);

    Page<MemberPoint> findByMemberIdAndState(Long memberId, int state, Pageable pageable);

    Optional<MemberPoint> findByMemberAndOrderAndPointAndState(Member member, Order order, int point, int state);

    @Query("SELECT memberPoint " +
            "FROM MemberPoint AS memberPoint " +
            "WHERE memberPoint.member.id = :memberId " +
            "   AND memberPoint.id >= :cursor " +
            "   AND memberPoint.state in :state " +
            "ORDER BY memberPoint.id ")
    List<MemberPoint> getAvailablePoint(
            @Param("memberId") long memberId,
            @Param("state") Set<Integer> state,
            @Param("cursor") Long cursor);


    // admin api
    Page<MemberPoint> findByStateAndOrderIsNull(int state, Pageable pageable);

    @Query("SELECT memberPoint " +
            "FROM MemberPoint as memberPoint " +
            "WHERE memberPoint.member.id = :memberId " +
            "   AND memberPoint.state in :state " +
            "   AND memberPoint.createdAt > :startAt " +
            "   AND memberPoint.createdAt < :endAt")
    List<MemberPoint> findByMemberPointHistory(long memberId, Set<Integer> state, Date startAt, Date endAt, Pageable pageable);

    @Query("SELECT memberPoint " +
            "FROM MemberPoint as memberPoint " +
            "WHERE memberPoint.member.id = :memberId " +
            "   AND memberPoint.state in :state " +
            "   AND memberPoint.expiryAt > :startAt " +
            "   AND memberPoint.expiryAt < :endAt")
    List<MemberPoint> findByExpiryPoint(long memberId, Set<Integer> state, Date startAt, Date endAt, Pageable pageable);

    boolean existsByActivityTypeAndMemberAndState(ActivityPointType type, Member member, int state);

    boolean existsByActivityTypeAndActivityDomainIdAndMemberAndState(ActivityPointType type,
                                                                     long domainId,
                                                                     Member member,
                                                                     int state);

    @Query("select case when(count(mp) < :limit) then true else false end from MemberPoint mp " +
            "where mp.member = :member and mp.activityType = :activity and mp.createdAt > current_date() and mp.state = 1")
    boolean isActivityTodayLessThanLimit(
            @Param("activity") ActivityPointType activity,
            @Param("limit") long limit,
            @Param("member") Member member);

    @Query("select case when(count(mp) < :limit) then true else false end from MemberPoint mp " +
            "where mp.activityType in :types and mp.createdAt > current_date() and mp.member = :member and mp.state = 1")
    boolean isActivityTodayLessThanLimit(
            @Param("types") List<ActivityPointType> types,
            @Param("limit") long limit,
            @Param("member") Member member);

    @Query("select mp.activityType from MemberPoint mp " +
            "where mp.activityType in :types and mp.activityDomainId = :domainId and mp.member = :member")
    Optional<ActivityPointType> findExactTypeByTypesAndDomainIdAndMember(
            @Param("types") List<ActivityPointType> types,
            @Param("domainId") Long domainId,
            @Param("member") Member member);
}
