package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CommunityRepository extends DefaultJpaRepository<Community, Long> {

    List<Community> findByCategoryInAndSortedAtLessThan(List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable);
    List<Community> findByEventIdAndCategoryInAndSortedAtLessThan(Long EventId, List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable);

}
