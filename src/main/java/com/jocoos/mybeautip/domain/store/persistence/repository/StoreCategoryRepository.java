package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreCategoryRepository extends DefaultJpaRepository<StoreCategory, Long> {

    boolean existsByCode(String code);
    Optional<StoreCategory> findTopByOrderBySortDesc();
}
