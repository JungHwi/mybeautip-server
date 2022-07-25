package com.jocoos.mybeautip.domain.term.persistence.repository;

import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    Optional<MemberTerm> findByTermIdAndMemberId(long termId, long memberId);
}
