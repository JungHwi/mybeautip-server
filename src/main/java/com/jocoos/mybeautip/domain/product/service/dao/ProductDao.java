package com.jocoos.mybeautip.domain.product.service.dao;

import com.jocoos.mybeautip.domain.product.persistence.domain.Product;
import com.jocoos.mybeautip.domain.product.persistence.repository.ProductRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ProductDao {

    private final ProductRepository productRepository;

    @Transactional
    public Product save(Product product) {
       return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("product not found id " + id));
    }

    @Transactional
    public void delete(Collection<Long> productIds) {
        productRepository.delete(productIds);
    }
}
