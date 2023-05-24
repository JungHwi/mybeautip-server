package com.jocoos.mybeautip.domain.company.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.vo.CompanyAccountVo;
import com.jocoos.mybeautip.domain.company.vo.CompanyClaimVo;
import com.jocoos.mybeautip.domain.company.vo.CompanyPermissionVo;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record CompanyResponse(long id,
                              String code,
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
                              @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt,
                              CompanyClaimVo claim,
                              CompanyPermissionVo permission,
                              List<CompanyAccountVo> accounts) {
}
