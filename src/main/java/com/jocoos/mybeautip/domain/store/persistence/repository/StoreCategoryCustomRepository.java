package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.jocoos.mybeautip.domain.store.dto.SearchStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryListResponse;
import org.springframework.data.domain.Page;

public interface StoreCategoryCustomRepository {

    Page<StoreCategoryListResponse> search(SearchStoreCategoryRequest request);
}
