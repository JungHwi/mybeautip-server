package com.jocoos.mybeautip.post;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendRepository extends JpaRepository<Trend, Long> {

  Slice<Trend> findByStartedAtAfterAndEndedAtBefore(Pageable pageable, Date now);
}
