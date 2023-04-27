package com.jocoos.mybeautip.domain.brand.converter;

import com.jocoos.mybeautip.domain.brand.dto.BrandListResponse;
import com.jocoos.mybeautip.domain.brand.dto.BrandResponse;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandConverter {

    BrandResponse converts(Brand brand);

    BrandListResponse convertsToListResponse(Brand brand);
    List<BrandListResponse> convertsToListResponse(List<Brand> brandList);
}
