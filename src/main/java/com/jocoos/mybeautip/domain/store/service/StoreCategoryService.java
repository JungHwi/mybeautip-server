package com.jocoos.mybeautip.domain.store.service;

import com.jocoos.mybeautip.domain.store.converter.StoreCategoryConverter;
import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.SearchStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryListResponse;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryResponse;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.domain.store.service.dao.StoreCategoryDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreCategoryService {

    private final StoreCategoryDao dao;
    private final StoreCategoryConverter converter;

    public StoreCategoryResponse create(CreateStoreCategoryRequest request) {
        StoreCategory storeCategory = dao.create(request);
        return converter.converts(storeCategory);
    }

    public Page<StoreCategoryListResponse> search(SearchStoreCategoryRequest request) {
        return dao.search(request);
    }

    public StoreCategoryResponse get(long categoryId) {
        StoreCategory storeCategory = dao.get(categoryId);
        return converter.converts(storeCategory);
    }
}
