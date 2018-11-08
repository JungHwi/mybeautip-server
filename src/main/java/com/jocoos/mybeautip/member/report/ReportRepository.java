package com.jocoos.mybeautip.member.report;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
  Optional<Report> findByMeIdAndYouId(long me, long you);
}