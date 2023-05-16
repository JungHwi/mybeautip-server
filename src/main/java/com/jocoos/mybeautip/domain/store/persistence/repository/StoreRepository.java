package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.store.persistence.domain.Store;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends ExtendedQuerydslJpaRepository<Store, Long>, StoreCustomRepository {

    @Modifying
    @Query("UPDATE Store s SET s.category.id = :newCategoryId WHERE s.category.id = :oldCategoryId")
    void changeCategory(@Param("oldCategoryId") long oldCategoryId, @Param("newCategoryId") long newCategoryId);

}
