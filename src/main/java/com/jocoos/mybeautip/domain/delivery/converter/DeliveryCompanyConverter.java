package com.jocoos.mybeautip.domain.delivery.converter;

import com.jocoos.mybeautip.domain.delivery.dto.DeliveryCompanyResponse;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryCompany;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryCompanyConverter {

    DeliveryCompanyResponse converts(DeliveryCompany entity);
    List<DeliveryCompanyResponse> converts(List<DeliveryCompany> entities);
}
