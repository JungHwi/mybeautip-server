package com.jocoos.mybeautip.domain.scrap.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends ExtendedQuerydslJpaRepository<Scrap, Long>, ScrapCustomRepository {

    Optional<Scrap> findByTypeAndMemberIdAndRelationId(ScrapType type, long memberId, long relationId);
    boolean existsByTypeAndMemberIdAndRelationIdAndIsScrap(ScrapType type, long memberId, long relationId, boolean isScrap);
    List<Scrap> findByTypeAndMemberIdAndIsScrapAndIdLessThan(ScrapType type, long memberId, boolean isScrap, long cursor, Pageable pageable);
    List<Scrap> findByTypeAndMemberIdAndRelationIdInAndIsScrap(ScrapType type, long memberId, List<Long> relationIds, boolean isScrap);
    boolean existsByMemberIdAndIsScrap(long memberId, boolean isScrap);
}
