package com.jocoos.mybeautip.domain.brand.persistence.repository;

import com.jocoos.mybeautip.domain.brand.dto.BrandSearchRequest;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import org.springframework.data.domain.Page;

public interface BrandCustomRepository {
    Page<Brand> search(BrandSearchRequest request);
}
