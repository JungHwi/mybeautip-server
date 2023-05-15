package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreCategoryRepository extends ExtendedQuerydslJpaRepository<StoreCategory, Long>, StoreCategoryCustomRepository {

    boolean existsByCode(String code);
    Optional<StoreCategory> findTopByOrderBySortDesc();
}
