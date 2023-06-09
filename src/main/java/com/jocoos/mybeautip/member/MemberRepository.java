package com.jocoos.mybeautip.member;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

public interface MemberRepository extends ExtendedQuerydslJpaRepository<Member, Long>, MemberCustomRepository {

    List<Member> findAllBy();

    List<Member> findByIdIn(Set<Long> ids);

    boolean existsByTag(String tag);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByIdAndLink(Long id, int link);

    List<Member> findByUsername(String username);

    List<Member> findByStatusAndLastLoggedAtLessThan(MemberStatus status, ZonedDateTime date);

    List<Member> findByStatusAndLastLoggedAtGreaterThanEqualAndLastLoggedAtLessThan(MemberStatus status, ZonedDateTime date1, ZonedDateTime date2);

    List<Member> findByStatusAndModifiedAtLessThan(MemberStatus status, Date date);

    Optional<Member> findByTag(String tag);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    Optional<Member> findByIdAndVisibleIsTrue(Long id);

    Optional<Member> findByUsernameAndDeletedAtIsNullAndVisibleIsTrue(String username);

    Optional<Member> findByUsernameAndLinkAndDeletedAtIsNull(String username, int link);

    Slice<Member> findByDeletedAtIsNullAndVisibleIsTrue(Pageable pageable);

    Slice<Member> findByCreatedAtBeforeAndDeletedAtIsNull(Date createdAt, Pageable pageable);

    Slice<Member> findByDeletedAtIsNullAndVisibleIsTrueAndUsernameContainingOrIntroContaining(String username, String intro, Pageable pageable);

    Slice<Member> findByCreatedAtBeforeAndDeletedAtIsNullAndVisibleIsTrueAndUsernameContainingOrIntroContaining(Date createdAt, String username, String intro, Pageable pageable);

    @Modifying
    @Query("UPDATE Member m set m.point = m.point + ?2 WHERE m.id = ?1")
    void updateMemberPoint(long memberId, int point);

    @Transactional
    @Modifying
    @Query("update Member m set m.lastLoggedAt = current_timestamp where m.id = ?1")
    void updateLastLoggedAt(Long memberId);

    @Modifying
    @Query("update Member m set m.followingCount = m.followingCount + ?2, m.modifiedAt = now() where m.id = ?1")
    void updateFollowingCount(Long id, Integer count);

    @Modifying
    @Query("update Member m set m.followerCount = m.followerCount + ?2, m.modifiedAt = now() where m.id = ?1")
    void updateFollowerCount(Long id, Integer count);

    @Modifying
    @Query("update Member m set m.reportCount = m.reportCount + ?2, m.modifiedAt = now() where m.id = ?1")
    void updateReportCount(Long id, Integer count);

    @Deprecated
    @Modifying
    @Query("update Member m set m.publicVideoCount = m.publicVideoCount + ?2, m.modifiedAt = now() where m.id = ?1")
    void updatePublicVideoCount(Long id, Integer count);

    @Deprecated
    @Modifying
    @Query("update Member m set m.totalVideoCount = m.totalVideoCount + ?2, m.modifiedAt = now() where m.id = ?1")
    void updateTotalVideoCount(Long id, Integer count);

    int countByVisibleIsTrueAndUsernameAndDeletedAtIsNull(String username);

    int countByUsernameAndLinkAndDeletedAtIsNull(String username, int link);

    @Modifying
    @Query("update Member m set m.revenue = m.revenue + ?2, m.modifiedAt = now() where m.id = ?1")
    void updateRevenue(Long id, Integer revenue);

    Page<Member> findByLinkInAndDeletedAtIsNull(Collection<Integer> links, Pageable pageable);

    Page<Member> findByDeletedAtIsNotNull(Pageable pageable);

    Page<Member> findByVisible(boolean visible, Pageable pageable);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    Page<Member> findByVisibleAndReportCountNot(boolean visible, int reportCount, Pageable pageable);

    Page<Member> findByLinkAndVisible(int link, boolean visible, Pageable pageable);

    Page<Member> findByLinkAndStatus(int link, MemberStatus status, Pageable pageable);

    Page<Member> findByVisibleAndUsernameContaining(boolean visible, String username, Pageable pageable);

    Page<Member> findByVisibleAndEmailContaining(boolean visible, String email, Pageable pageable);

    Page<Member> findByVisibleAndPushableAndUsernameContaining(boolean visible, boolean pushable, String username, Pageable pageable);

    Page<Member> findByStatusAndUsernameContaining(MemberStatus status, String username, Pageable pageable);

    Page<Member> findByStatusAndEmailContaining(MemberStatus status, String email, Pageable pageable);

    Page<Member> findByVisibleAndPushable(boolean visible, boolean pushable, Pageable pageable);

    List<Member> findByVisibleAndDeletedAtIsNull(boolean visible, Pageable pageable);

    List<Member> findAllByVisibleTrueAndPushableTrue();

    @Query("select f.id as followingId, r.id as reportedId, b.id as blockedId from Member m" +
            "  left outer join Following f on m.id=f.memberMe.id and f.memberYou.id=?2" +
            "  left outer join Report r on m.id=r.me.id and r.you.id=?2" +
            "  left outer join Block b on m.id=b.me and b.memberYou.id=?2" +
            "  where m.id=?1")
    Optional<MemberExtraInfo> findMemberExtraInfo(Long me, Long you);

    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

    List<Member> findByVisibleIsTrueAndPushableIsTrue();

    List<Member> findByVisibleIsTrueAndPushableIsTrueAndLastLoggedAtLessThan(ZonedDateTime localDateTime);

    @Query("SELECT m FROM Member m WHERE LENGTH(m.tag) < 1")
    List<Member> selectTagIsEmpty();

    @Query("select count(m) from Member m where m.id in :ids")
    long countByIdIn(@Param("ids") Set<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "insert into members (id, username, avatar_url, tag, permission, created_at) VALUES (?1, ?2, ?3, ?4, 19, now())", nativeQuery = true)
    void insert(Long id, String username, String avatarUrl, String tag);
}
