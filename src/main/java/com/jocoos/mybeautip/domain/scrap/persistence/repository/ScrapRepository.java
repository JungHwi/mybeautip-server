package com.jocoos.mybeautip.domain.scrap.persistence.repository;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends DefaultJpaRepository<Scrap, Long> {

    Optional<Scrap> findByTypeAndMemberIdAndRelationId(ScrapType type, long memberId, long relationId);
    boolean existsByTypeAndMemberIdAndRelationIdAndIsScrap(ScrapType type, long memberId, long relationId, boolean isScrap);
    List<Scrap> findByTypeAndIdLessThan(ScrapType type, long cursor, Pageable pageable);
    List<Scrap> findByTypeAndMemberIdAndRelationIdInAndIsScrap(ScrapType type, long memberId, List<Long> relationIds, boolean isScrap);
}
