package com.jocoos.mybeautip.domain.product.persistence.repository;

import com.jocoos.mybeautip.domain.product.persistence.domain.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
}
