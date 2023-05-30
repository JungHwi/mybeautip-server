package com.jocoos.mybeautip.domain.product.dto;

import java.util.Set;

public record DeleteProductRequest(Set<Long> productIds) {
}
