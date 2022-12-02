package com.jocoos.mybeautip.domain.term.persistence.repository;

import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    Optional<MemberTerm> findByTermIdAndMemberId(long termId, long memberId);

    Optional<MemberTerm> findFirstByMemberIdOrderByModifiedAtDesc(long memberId);

    List<MemberTerm> findAllByMemberId(long memberId);

    List<MemberTerm> findAllByMemberIdAndTermIdIn(long memberId, List<Long> termIds);

    @Modifying
    @Query("update MemberTerm mt set mt.isAccept = :isAccept where mt.member.id = :memberId and mt.term.id = :termId")
    Optional<MemberTerm> updateIsAccept(@Param("memberId") long memberId,
                                        @Param("termId") long termId,
                                        @Param("isAccept") boolean isAccept);

    List<MemberTerm> findAllByMemberIdAndTermIn(long memberId, List<Term> terms);

    List<MemberTerm> findByTermIdAndMemberIdIn(long marketingTermId, List<Long> memberIds);
}
