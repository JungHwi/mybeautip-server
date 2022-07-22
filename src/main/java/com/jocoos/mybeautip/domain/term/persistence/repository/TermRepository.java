package com.jocoos.mybeautip.domain.term.persistence.repository;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import com.jocoos.mybeautip.domain.term.code.TermUsedType;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findAllByUsedTypeAndCurrentTermStatusNot(TermUsedType type, TermStatus status);
}
