package com.jocoos.mybeautip.domain.store.service.dao;

import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.SearchStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryListResponse;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.domain.store.persistence.repository.StoreCategoryRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreCategoryDao {

    private final StoreCategoryRepository repository;

    public StoreCategory create(CreateStoreCategoryRequest request) {
        String code = generateCode();
        int sort = repository.findTopByOrderBySortDesc()
                .orElse(new StoreCategory())
                .getSort() + 1;
        StoreCategory storeCategory = new StoreCategory(request, code, sort);
        return repository.save(storeCategory);
    }

    public Page<StoreCategoryListResponse> search(SearchStoreCategoryRequest request) {
        return repository.search(request);
    }

    private String generateCode() {
        for (int index = 0; index < 5; index++) {
            String code = RandomUtils.generateStoreCategoryCode();
            if (!repository.existsByCode(code)) {
                return code;
            }
        }
        throw new BadRequestException("Store Category Code 생성에 실패하였습니다.");
    }

}
