package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;

public interface VodRepository extends ExtendedQuerydslJpaRepository<Vod, Long>, VodCustomRepository {
}
