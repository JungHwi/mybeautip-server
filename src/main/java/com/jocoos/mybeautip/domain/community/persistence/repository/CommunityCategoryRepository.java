package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityCategoryRepository extends DefaultJpaRepository<CommunityCategory, Long> {

    Optional<CommunityCategory> findByType(CommunityCategoryType type);

    List<CommunityCategory> findAllBy(Pageable pageable);

    List<CommunityCategory> findAllByIdIn(List<Long> categoryIds);

    List<CommunityCategory> findAllByParentId(Long parentId);
}


