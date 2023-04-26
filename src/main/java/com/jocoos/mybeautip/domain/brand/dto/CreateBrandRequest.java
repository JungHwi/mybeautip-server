package com.jocoos.mybeautip.domain.brand.dto;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class CreateBrandRequest {

    long companyId;

    @Setter
    String code;

    BrandStatus status;

    String name;

    String description;
}
