package com.jocoos.mybeautip.member.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
  Optional<Report> findByMeIdAndYouId(long me, long you);
}