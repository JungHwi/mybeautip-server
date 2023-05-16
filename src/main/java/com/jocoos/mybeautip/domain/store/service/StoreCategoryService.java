package com.jocoos.mybeautip.domain.store.service;

import com.jocoos.mybeautip.domain.store.converter.StoreCategoryConverter;
import com.jocoos.mybeautip.domain.store.dto.*;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.domain.store.service.dao.StoreCategoryDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreCategoryService {

    private final StoreCategoryDao dao;
    private final StoreCategoryConverter converter;

    @Transactional
    public StoreCategoryResponse create(CreateStoreCategoryRequest request) {
        StoreCategory storeCategory = dao.create(request);
        return converter.converts(storeCategory);
    }

    @Transactional(readOnly = true)
    public Page<StoreCategoryListResponse> search(SearchStoreCategoryRequest request) {
        return dao.search(request);
    }

    @Transactional(readOnly = true)
    public StoreCategoryResponse get(long categoryId) {
        StoreCategory storeCategory = dao.get(categoryId);
        return converter.converts(storeCategory);
    }

    @Transactional
    public StoreCategoryResponse edit(long categoryId, EditStoreCategoryRequest request) {
        StoreCategory storeCategory = dao.get(categoryId);
        storeCategory.edit(request);
        return converter.converts(storeCategory);
    }
}
