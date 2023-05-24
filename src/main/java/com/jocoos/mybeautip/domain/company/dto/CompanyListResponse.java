package com.jocoos.mybeautip.domain.company.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.vo.CompanyPermissionVo;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record CompanyListResponse(long id,
                                  String code,
                                  String name,
                                  CompanyStatus status,
                                  float salesFee,
                                  float shippingFee,
                                  CompanyPermissionVo permission,
                                  @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt) {
}
