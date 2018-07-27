package com.jocoos.mybeautip.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
  Optional<StoreLike> findByStoreIdAndCreatedBy(Long storeId, Long memberId);

  Long countByStoreId(Long id);
}