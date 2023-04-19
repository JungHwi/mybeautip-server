package com.jocoos.mybeautip.domain.company.converter;

import com.jocoos.mybeautip.domain.company.persistence.domain.CompanyClaim;
import com.jocoos.mybeautip.domain.company.vo.CompanyClaimVo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyClaimConverter {

    CompanyClaimVo converts(CompanyClaim claim);
}
