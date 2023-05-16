package com.jocoos.mybeautip.domain.store.service.dao;

import com.jocoos.mybeautip.domain.store.persistence.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreDao {

    private final StoreRepository repository;

    @Transactional
    public void changeCategory(long oldStoreCategoryId, long newStoreCategoryId) {
        repository.changeCategory(oldStoreCategoryId, newStoreCategoryId);
    }
}
