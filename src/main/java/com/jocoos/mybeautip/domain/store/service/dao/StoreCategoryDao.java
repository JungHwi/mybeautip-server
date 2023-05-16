package com.jocoos.mybeautip.domain.store.service.dao;

import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.SearchStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryListResponse;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.domain.store.persistence.repository.StoreCategoryRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.exception.ErrorCode.STORE_CATEGORY_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class StoreCategoryDao {

    private final StoreCategoryRepository repository;

    @Transactional
    public StoreCategory create(CreateStoreCategoryRequest request) {
        String code = generateCode();
        int sort = repository.findTopByOrderBySortDesc()
                .orElse(new StoreCategory())
                .getSort() + 1;
        StoreCategory storeCategory = new StoreCategory(request, code, sort);
        return repository.save(storeCategory);
    }

    @Transactional(readOnly = true)
    public Page<StoreCategoryListResponse> search(SearchStoreCategoryRequest request) {
        return repository.search(request);
    }

    @Transactional(readOnly = true)
    public StoreCategory get(long categoryId) {
        return repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(STORE_CATEGORY_NOT_FOUND, "Store category not found. id - " + categoryId));
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
