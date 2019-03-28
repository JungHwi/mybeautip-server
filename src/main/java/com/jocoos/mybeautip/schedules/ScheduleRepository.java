package com.jocoos.mybeautip.schedules;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  Page<Schedule> findByStartedAtAfterAndDeletedAtIsNull(Date startedAt, Pageable pageable);

  List<Schedule> findByCreatedByIdOrderByStartedAt(Long id);

  Optional<Schedule> findByCreatedByIdAndStartedAtBetweenAndDeletedAtIsNull(Long id, Date from, Date to);
}