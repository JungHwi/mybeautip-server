package com.jocoos.mybeautip.domain.product.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.product.persistence.domain.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ProductRepository extends ExtendedQuerydslJpaRepository<Product, Long> {

    @Modifying
    @Query("UPDATE Product product SET product.status = 'DELETE' WHERE product.id IN :ids")
    void delete(@Param("ids") Collection<Long> productIds);
}
