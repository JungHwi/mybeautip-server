package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByUsername(String username);

  Slice<Member> findByDeletedAtIsNull(Pageable pageable);

  Slice<Member> findByCreatedAtBeforeAndDeletedAtIsNull(Date createdAt, Pageable pageable);

  Slice<Member> findByUsernameContainingOrIntroContainingAndDeletedAtIsNull(String username, String intro, Pageable pageable);

  Slice<Member> findByUsernameContainingOrIntroContainingAndCreatedAtBeforeAndDeletedAtIsNull(String username, String intro, Date createdAt, Pageable pageable);
}
