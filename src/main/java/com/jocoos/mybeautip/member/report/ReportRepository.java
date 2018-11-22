package com.jocoos.mybeautip.member.report;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
  Optional<Report> findByMeIdAndYouId(long me, long you);

  Page<Report> findByYouId(Long memberId, Pageable pageable);

}