package com.jocoos.mybeautip.domain.brand.dto;

import com.jocoos.mybeautip.domain.company.dto.SimpleCompanyResponse;

public record BrandResponse(long id,
                            SimpleCompanyResponse company,
                            String code,
                            String status,
                            String name,
                            String description) {
}
