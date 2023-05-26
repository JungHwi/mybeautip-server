package com.jocoos.mybeautip.domain.product.persistence.repository;

import com.jocoos.mybeautip.domain.product.persistence.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
