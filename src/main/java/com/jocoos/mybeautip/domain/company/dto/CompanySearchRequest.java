package com.jocoos.mybeautip.domain.company.dto;

import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.Set;

@Builder
public record CompanySearchRequest(String name,
                                   Set<CompanyStatus> status,
                                   Pageable pageable) {
}
