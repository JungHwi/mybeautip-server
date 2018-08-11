package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByUsername(String username);

  Slice<Member> findByDeletedAtIsNull(Pageable pageable);

  Slice<Member> findByCreatedAtBeforeAndDeletedAtIsNull(Date createdAt, Pageable pageable);

  Slice<Member> findByUsernameContainingOrIntroContainingAndDeletedAtIsNull(String username, String intro, Pageable pageable);

  Slice<Member> findByUsernameContainingOrIntroContainingAndCreatedAtBeforeAndDeletedAtIsNull(String username, String intro, Date createdAt, Pageable pageable);

  @Modifying
  @Query("update Member m set m.followingCount = m.followingCount + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateFollowingCount(Long id, Integer count);

  @Modifying
  @Query("update Member m set m.followerCount = m.followerCount + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateFollowerCount(Long id, Integer count);

  int countByUsernameAndDeletedAtIsNull(String username);

  @Modifying
  @Query("update Member m set m.videoCount = m.videoCount + ?2, m.modifiedAt = now() where m.id = ?1")
  void updateVideoCount(Long id, Integer count);
}
