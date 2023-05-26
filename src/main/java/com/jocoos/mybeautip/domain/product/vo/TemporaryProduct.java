package com.jocoos.mybeautip.domain.product.vo;

import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;

import java.util.List;

public record TemporaryProduct(Long id,
                               String name,
                               Long stock,
                               int weight,
                               Brand brand,
                               List<String> imageUrls) {
}
