package com.jocoos.mybeautip.schedules;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Page<Schedule> findByStartedAtAfterAndDeletedAtIsNull(Date startedAt, Pageable pageable);

    Page<Schedule> findByStartedAtBeforeAndDeletedAtIsNull(Date startedAt, Pageable pageable);

    List<Schedule> findByCreatedByIdOrderByStartedAt(Long id);

    Optional<Schedule> findTopByCreatedByIdAndStartedAtBetweenAndDeletedAtIsNull(Long id, Date from, Date to);

    Page<Schedule> findByCreatedByIdAndStartedAtBeforeAndDeletedAtIsNull(Long id, Date startedAt, Pageable pageable);

    Optional<Schedule> findByIdAndCreatedById(Long id, Long memberId);

    Page<Schedule> findByStartedAtAfterAndDeletedAtIsNullOrderByStartedAt(Date startedAt, Pageable pageable);

}
