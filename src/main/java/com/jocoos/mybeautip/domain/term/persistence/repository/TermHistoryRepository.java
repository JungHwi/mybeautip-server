package com.jocoos.mybeautip.domain.term.persistence.repository;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import com.jocoos.mybeautip.domain.term.persistence.domain.TermHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TermHistoryRepository extends JpaRepository<TermHistory, Long> {

    @Query("select distinct th.termId from TermHistory th " +
            "where th.termId in :termIds and th.versionChangeStatus = :status")
    List<Long> findVersionChangeHistoryIn(@Param("status") TermStatus status, @Param("termIds") List<Long> termIds);
}
