package com.jocoos.mybeautip.domain.product.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.product.persistence.domain.Product;

public interface ProductRepository extends ExtendedQuerydslJpaRepository<Product, Long> {
}
