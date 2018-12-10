package com.jocoos.mybeautip.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByIdAndDeletedAtIsNull(Long id);

  Slice<Member> findByDeletedAtIsNullAndVisibleIsTrue(Pageable pageable);

  Slice<Member> findByCreatedAtBeforeAndDeletedAtIsNull(Date createdAt, Pageable pageable);

  Slice<Member> findByDeletedAtIsNullAndVisibleIsTrueAndUsernameContainingOrIntroContaining(String username, String intro, Pageable pageable);

  Slice<Member> findByCreatedAtBeforeAndDeletedAtIsNullAndVisibleIsTrueAndUsernameContainingOrIntroContaining(Date createdAt, String username, String intro, Pageable pageable);

  @Modifying
  @Query("update Member m set m.followingCount = m.followingCount + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateFollowingCount(Long id, Integer count);

  @Modifying
  @Query("update Member m set m.followerCount = m.followerCount + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateFollowerCount(Long id, Integer count);

  @Modifying
  @Query("update Member m set m.reportCount = m.reportCount + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateReportCount(Long id, Integer count);

  int countByUsernameAndDeletedAtIsNull(String username);

  @Modifying
  @Query("update Member m set m.videoCount = ?2, m.modifiedAt = now() where m.id = ?1")
  void updateVideoCount(Long id, Integer count);

  @Modifying
  @Query("update Member m set m.totalVideoCount = ?2, m.modifiedAt = now() where m.id = ?1")
  void updateTotalVideoCount(Long id, Integer count);

  @Modifying
  @Query("update Member m set m.revenue = m.revenue + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateRevenue(Long id, Integer revenue);

  Page<Member> findByLinkInAndDeletedAtIsNull(Collection<Integer> links, Pageable pageable);

  Page<Member> findByDeletedAtIsNotNull(Pageable pageable);

  Page<Member> findByLinkInAndEmailIsNotNullAndDeletedAtIsNotNull(Collection<Integer> links, Pageable pageable);

  Page<Member> findByLinkInAndPushableAndDeletedAtIsNull(Collection<Integer> links, boolean pushable, Pageable pageable);

  Page<Member> findByLinkInAndPushableAndDeletedAtIsNullAndUsernameContaining(Collection<Integer> links, boolean pushable, String username, Pageable pageable);

}
