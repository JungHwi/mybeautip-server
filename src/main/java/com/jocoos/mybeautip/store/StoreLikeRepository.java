package com.jocoos.mybeautip.store;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
  Optional<StoreLike> findByStoreIdAndCreatedBy(Long storeId, Long memberId);

  Optional<StoreLike> findByIdAndStoreIdAndCreatedBy(Long id, Long storeId, Long createdBy);

  Slice<StoreLike> findByCreatedAtBeforeAndCreatedBy(Date createdAt, Long createdBy, Pageable pageable);

  Slice<StoreLike> findByCreatedBy(Long createdBy, Pageable pageable);

  Integer countByCreatedBy(Long createdBy);
}