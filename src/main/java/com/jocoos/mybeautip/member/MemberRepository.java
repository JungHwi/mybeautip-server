package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByUsername(String username);

  @Query("select m from Member m where m.deletedAt is null")
  Slice<Member> findMembers(Pageable pageable);

  @Query("select m from Member m where m.deletedAt is null and m.createdAt < ?1")
  Slice<Member> findMembersByCursor(Date cursor, Pageable pageable);

  @Query("select m from Member m where m.deletedAt is null and m.username like %?1%")
  Slice<Member> findMembersByKeyword(String keyword, Pageable pageable);

  @Query("select m from Member m where m.deletedAt is null and m.username like %?1% and m.createdAt < ?2")
  Slice<Member> findMembersByKeywordAndCursor(String keyword, Date cursor, Pageable pageable);

}
