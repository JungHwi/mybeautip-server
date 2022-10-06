package com.jocoos.mybeautip.domain.scrap.persistence.repository;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScrapRepository extends DefaultJpaRepository<Scrap, Long> {

    List<Scrap> findByTypeAndIdLessThan(ScrapType type, long cursor, Pageable pageable);
}