package com.jocoos.mybeautip.domain.placard.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PlacardRepository extends ExtendedQuerydslJpaRepository<Placard, Long>, PlacardCustomRepository {

    List<Placard> findAllByStartedAtLessThanEqualAndEndedAtGreaterThanAndStatus(ZonedDateTime startAt, ZonedDateTime endAt, PlacardStatus status);

    List<Placard> findAllByEndedAtLessThanEqualAndStatus(ZonedDateTime now, PlacardStatus status);
}
