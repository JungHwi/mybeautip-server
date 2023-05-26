package com.jocoos.mybeautip.domain.product.dto;

public record ProductCreateRequest(Long id,
                                   String name,
                                   Long stock,
                                   Integer weight,
                                   Long brandId) {
}
