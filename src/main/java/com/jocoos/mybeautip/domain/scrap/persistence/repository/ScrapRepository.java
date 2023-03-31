package com.jocoos.mybeautip.domain.scrap.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends ExtendedQuerydslJpaRepository<Scrap, Long>, ScrapCustomRepository {

    Optional<Scrap> findByTypeAndMemberIdAndRelationId(ScrapType type, long memberId, long relationId);
    List<Scrap> findByTypeAndMemberIdAndIsScrap(ScrapType type, Long memberId, boolean isScrap);
    boolean existsByTypeAndMemberIdAndRelationIdAndIsScrap(ScrapType type, long memberId, long relationId, boolean isScrap);
    List<Scrap> findByTypeAndMemberIdAndRelationIdInAndIsScrap(ScrapType type, long memberId, List<Long> relationIds, boolean isScrap);
    boolean existsByMemberIdAndIsScrap(long memberId, boolean isScrap);
}
