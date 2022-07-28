package com.jocoos.mybeautip.domain.term.persistence.repository;

import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TermRepository extends JpaRepository<Term, Long> {

    // 상태 축소하면서 삭제 상태 제외, 추후 추가
    @Query("select t from Term t where :type in t.usedInType")
    List<Term> findAllByUsedIn(@Param("type") String type);

    boolean existsById(long termId);
    List<Term> findAllByIdIn(List<Long> termIds);

    Optional<Term> findTopByOrderByModifiedAtDesc();

    // 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
    List<Term> findAllByTypeIn(Set<TermType> termTypes);
    Optional<Term> findByType(TermType type);
}
