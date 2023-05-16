package com.jocoos.mybeautip.domain.store.dto;

import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class StoreCategoryListResponse {

    private long id;
    private String name;
    private int sort;
    private long displayCount;

    @QueryProjection
    public StoreCategoryListResponse(StoreCategory storeCategory,
                                     long displayCount) {

        this.id = storeCategory.getId();
        this.name = storeCategory.getName();
        this.sort = storeCategory.getSort();
        this.displayCount = displayCount;
    }
}
