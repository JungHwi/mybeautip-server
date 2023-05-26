package com.jocoos.mybeautip.domain.product.converter;

import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.product.dto.ProductCreateRequest;
import com.jocoos.mybeautip.domain.product.dto.ProductTempRequest;
import com.jocoos.mybeautip.domain.product.persistence.domain.Product;
import com.jocoos.mybeautip.domain.product.vo.TemporaryProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductConverter {

    @Mappings({
            @Mapping(target = "id", source = "request.id"),
            @Mapping(target = "name", source = "request.name")
    })
    TemporaryProduct convert(ProductTempRequest request, Brand brand, List<String> imageUrls);

    TemporaryProduct convert(ProductTempRequest request, List<String> imageUrls);

    Product convert(ProductCreateRequest request);
}
