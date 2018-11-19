package com.jocoos.mybeautip.store;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
  Optional<StoreLike> findByStoreIdAndCreatedById(Integer storeId, Long memberId);

  Optional<StoreLike> findByIdAndStoreIdAndCreatedById(Long id, Integer storeId, Long createdBy);

  Slice<StoreLike> findByCreatedAtBeforeAndCreatedById(Date createdAt, Long createdBy, Pageable pageable);

  Slice<StoreLike> findByCreatedById(Long createdBy, Pageable pageable);

  Integer countByCreatedById(Long createdBy);
}