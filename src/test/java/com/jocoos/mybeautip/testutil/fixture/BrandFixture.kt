package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.brand.code.BrandStatus
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand
import com.jocoos.mybeautip.domain.company.persistence.domain.Company
import com.jocoos.mybeautip.support.RandomUtils

fun makeBrand(
    company: Company,
    request: CreateBrandRequest = makeBrandRequest(company)
): Brand {
    return Brand(request, company)
}

fun makeBrandRequest(
    company: Company,
    name: String = "브랜드명이다~",
    code: String = RandomUtils.generateBrandCode(),
    status: BrandStatus = BrandStatus.ACTIVE,
    description: String? = "브랜드 설명"
): CreateBrandRequest {
    return CreateBrandRequest.builder()
        .companyId(company.id)
        .code(code)
        .status(status)
        .name(name)
        .description(description)
        .build()
}