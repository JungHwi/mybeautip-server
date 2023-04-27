package com.jocoos.mybeautip.domain.brand.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends ExtendedQuerydslJpaRepository<Brand, Long>, BrandCustomRepository {

    boolean existsByCode(String code);

}
