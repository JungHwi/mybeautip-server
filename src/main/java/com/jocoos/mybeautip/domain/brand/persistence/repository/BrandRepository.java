package com.jocoos.mybeautip.domain.brand.persistence.repository;

import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends DefaultJpaRepository<Brand, Long> {

    boolean existsByCode(String code);
}
