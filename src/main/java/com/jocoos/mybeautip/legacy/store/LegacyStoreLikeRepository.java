package com.jocoos.mybeautip.legacy.store;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface LegacyStoreLikeRepository extends JpaRepository<LegacyStoreLike, Long> {
    Optional<LegacyStoreLike> findByLegacyStoreIdAndCreatedById(Integer storeId, Long memberId);

    Optional<LegacyStoreLike> findByIdAndLegacyStoreIdAndCreatedById(Long id, Integer storeId, Long createdBy);

    Slice<LegacyStoreLike> findByCreatedAtBeforeAndCreatedById(Date createdAt, Long createdBy, Pageable pageable);

    Slice<LegacyStoreLike> findByCreatedById(Long createdBy, Pageable pageable);

    Integer countByCreatedById(Long createdBy);
}