package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreCategoryRepository extends ExtendedQuerydslJpaRepository<StoreCategory, Long>, StoreCategoryCustomRepository {

    boolean existsByCode(String code);
    Optional<StoreCategory> findTopByOrderBySortDesc();

    @Modifying
    @Query("UPDATE StoreCategory category SET category.sort = category.sort + :adjustSort WHERE category.sort >= :minSort AND category.sort <= :maxSort")
    void changeSort(@Param("minSort") int minSort, @Param("maxSort") int maxSort, @Param("adjustSort") int adjustSort);
}
