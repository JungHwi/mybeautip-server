package com.jocoos.mybeautip.domain.brand.converter;

import com.jocoos.mybeautip.domain.brand.dto.BrandResponse;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandConverter {

    BrandResponse converts(Brand brand);
}
