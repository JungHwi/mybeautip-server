package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.company.persistence.domain.Company
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeStatus
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType
import com.jocoos.mybeautip.domain.delivery.code.DeliveryMethod
import com.jocoos.mybeautip.domain.delivery.code.PaymentOption
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyDetailRequest
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyRequest
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy
import com.jocoos.mybeautip.global.code.CountryCode

fun makeDeliveryFeePolicy(
    company: Company,
    request: CreateDeliveryFeePolicyRequest? = makeCreateDeliveryFeePolicyRequest(company.id)
): DeliveryFeePolicy {
    return DeliveryFeePolicy(company, request)
}

fun makeCreateDeliveryFeePolicyRequest (
    companyId: Long,
    name: String? = "대표 배송비명",
    status: DeliveryFeeStatus? = DeliveryFeeStatus.ACTIVE,
    type: DeliveryFeeType? = DeliveryFeeType.AMOUNT,
    deliveryMethod: DeliveryMethod? = DeliveryMethod.COURIER,
    paymentOption: PaymentOption? = PaymentOption.PREPAID,
    details: List<CreateDeliveryFeePolicyDetailRequest>? = makeCreateDeliveryFeePolicyDetailRequestList()
): CreateDeliveryFeePolicyRequest {
    return CreateDeliveryFeePolicyRequest.builder()
        .companyId(companyId)
        .name(name)
        .status(status)
        .type(type)
        .deliveryMethod(deliveryMethod)
        .paymentOption(paymentOption)
        .details(details)
        .build()
}

fun makeCreateDeliveryFeePolicyDetailRequestList(): List<CreateDeliveryFeePolicyDetailRequest> {
    return mutableListOf(
        makeCreateDeliveryFeePolicyDetailRequest(countryCode = CountryCode.KR, name = "한국 배송비명", threshold = 50000, feeAboveThreshold = 0, feeBelowThreshold = 3500),
        makeCreateDeliveryFeePolicyDetailRequest(countryCode = CountryCode.TH, name = "태국 배송비명", threshold = 100000, feeAboveThreshold = 0, feeBelowThreshold = 2000),
        makeCreateDeliveryFeePolicyDetailRequest(countryCode = CountryCode.VN, name = "베트남 배송비명", threshold = 200000, feeAboveThreshold = 0, feeBelowThreshold = 500)
    )
}

fun makeCreateDeliveryFeePolicyDetailRequest(
    countryCode: CountryCode? = CountryCode.KR,
    name: String? = "한국 배송비명",
    threshold: Int?,
    feeBelowThreshold: Int = 0,
    feeAboveThreshold: Int = 0
): CreateDeliveryFeePolicyDetailRequest {
    return CreateDeliveryFeePolicyDetailRequest.builder()
        .countryCode(countryCode)
        .name(name)
        .threshold(threshold)
        .feeBelowThreshold(feeBelowThreshold)
        .feeAboveThreshold(feeAboveThreshold)
        .build()
}