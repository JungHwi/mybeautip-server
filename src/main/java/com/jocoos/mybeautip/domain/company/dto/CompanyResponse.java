package com.jocoos.mybeautip.domain.company.dto;

import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.vo.CompanyAccountVo;
import com.jocoos.mybeautip.domain.company.vo.CompanyClaimVo;

import java.util.List;

public record CompanyResponse(long id,
                              String name,
                              CompanyStatus status,
                              float salesFee,
                              float shippingFee,
                              String businessName,
                              String businessNumber,
                              String representativeName,
                              String email,
                              String phoneNumber,
                              String businessType,
                              String businessItem,
                              String zipcode,
                              String address1,
                              String address2,
                              CompanyClaimVo claim,
                              List<CompanyAccountVo> accounts) {
}
