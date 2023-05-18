package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryCompanyRequest
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryCompany

fun makeDeliveryCompany(
    request: CreateDeliveryCompanyRequest? = makeCreateDeliveryCompanyRequest()
): DeliveryCompany {
    return DeliveryCompany(request)
}

fun makeCreateDeliveryCompanyRequest (
    status: DeliveryCompanyStatus? = DeliveryCompanyStatus.ACTIVE,
    name: String = "엄청 큰 택배회사",
    url: String = "https://www.cjlogistics.com/ko/tool/parcel/tracking"
): CreateDeliveryCompanyRequest {
    return CreateDeliveryCompanyRequest.builder()
        .status(status)
        .name(name)
        .url(url)
        .build()
}