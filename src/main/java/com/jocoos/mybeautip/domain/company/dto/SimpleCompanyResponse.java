package com.jocoos.mybeautip.domain.company.dto;

import com.jocoos.mybeautip.domain.company.code.CompanyStatus;

public record SimpleCompanyResponse(long id,
                                    String code,
                                    String name,
                                    CompanyStatus status) {
}
