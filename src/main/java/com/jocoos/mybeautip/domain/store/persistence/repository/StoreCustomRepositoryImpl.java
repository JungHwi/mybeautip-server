package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.store.persistence.domain.Store;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class StoreCustomRepositoryImpl implements StoreCustomRepository {

    private final ExtendedQuerydslJpaRepository<Store, Long> repository;

    public StoreCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Store, Long> repository) {
        this.repository = repository;
    }
}
