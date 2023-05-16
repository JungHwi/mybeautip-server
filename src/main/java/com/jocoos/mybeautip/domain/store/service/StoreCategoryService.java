package com.jocoos.mybeautip.domain.store.service;

import com.jocoos.mybeautip.domain.store.converter.StoreCategoryConverter;
import com.jocoos.mybeautip.domain.store.dto.*;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.domain.store.service.dao.StoreCategoryDao;
import com.jocoos.mybeautip.domain.store.service.dao.StoreDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreCategoryService {

    private final StoreCategoryDao dao;
    private final StoreDao storeDao;
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

    @Transactional
    public void delete(long categoryId, long newCategoryId) {
        storeDao.changeCategory(categoryId, newCategoryId);
        StoreCategory oldStoreCategory = dao.get(categoryId);
        oldStoreCategory.delete();
    }

    @Transactional
    public StoreCategoryResponse changeSort(long categoryId, int sort) {
        StoreCategory storeCategory = dao.changeSort(categoryId, sort);

        return converter.converts(storeCategory);
    }
}
