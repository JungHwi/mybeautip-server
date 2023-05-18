package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus;

public record DeliveryCompanyResponse(long id,
                                      String code,
                                      DeliveryCompanyStatus status,
                                      String name,
                                      String url) {
}
