package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.Optional;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

@GraphQlRepository
public interface VodRepository extends ExtendedQuerydslJpaRepository<Vod, Long>, VodCustomRepository {

    @Lock(value = PESSIMISTIC_WRITE)
    @Query("select v from Vod v where v.id = :id")
    Optional<Vod> selectForUpdate(@Param("id") Long id);

    Optional<Vod> findByVideoKey(Long videoKey);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Vod v set v.reportCount = v.reportCount + :count where v.id = :id")
    void addReportCount(@Param("id") long vodId,
                        @Param("count") int count);

}
