package com.jocoos.mybeautip.domain.store.converter;

import com.jocoos.mybeautip.domain.store.dto.StoreCategoryResponse;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreCategoryConverter {

    StoreCategoryResponse converts(StoreCategory storeCategory);
}
