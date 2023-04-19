package com.jocoos.mybeautip.domain.company.converter;

import com.jocoos.mybeautip.domain.company.dto.CompanyResponse;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyConverter {
    CompanyResponse converts(Company company);

}
