package com.jocoos.mybeautip.member.report;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends CrudRepository<Report, Long> {
  Optional<Report> findByMeIdAndYouId(long me, long you);
}