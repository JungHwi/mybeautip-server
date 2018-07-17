package com.jocoos.mybeautip.member.report;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReportRepository extends CrudRepository<Report, Long> {
  Optional<Report> findByMeAndYou(long me, long you);
}