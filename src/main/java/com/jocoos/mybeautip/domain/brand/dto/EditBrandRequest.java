package com.jocoos.mybeautip.domain.brand.dto;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import lombok.Builder;

@Builder
public record EditBrandRequest(BrandStatus status,
                               String name,
                               String description) {

}
