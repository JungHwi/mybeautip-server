package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.company.code.CompanyStatus
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest
import com.jocoos.mybeautip.domain.company.persistence.domain.Company
import com.jocoos.mybeautip.domain.company.vo.CompanyClaimVo
import com.jocoos.mybeautip.global.vo.AccountVo

fun makeCompany(
    request: CreateCompanyRequest = makeCompanyRequest()
): Company {
    val company = Company(request)
    return company
}

fun makeCompanyRequest(
    name: String = "고옹급",
    status: CompanyStatus = CompanyStatus.ACTIVE,
    salesFee: Float = 5.0f,
    shippingFee: Float = 5.0f,
    businessName: String = "사앙호",
    businessNumber: String = "1231231234",
    representativeName: String = "김대표",
    email: String = "email@address.com",
    phoneNumber: String = "010-2345-6789",
    businessType: String = "업태",
    businessItem: String = "업종",
    zipcode: String = "012345",
    address1: String = "태양계 지구 한국",
    address2: String = "서울 강남구",
    claim: CompanyClaimVo = createClaim(),
    accounts: List<AccountVo> = createAccount()
): CreateCompanyRequest {
    return CreateCompanyRequest(
        name,
        status,
        salesFee,
        shippingFee,
        businessName,
        businessNumber,
        representativeName,
        email,
        phoneNumber,
        businessType,
        businessItem,
        zipcode,
        address1,
        address2,
        claim,
        accounts
    )
}

fun createClaim(
    customerCenterPhone: String = "02-1234-1234",
    zipcode: String = "123456",
    address1: String = "태양계 화성",
    address2: String = "데이모스"
): CompanyClaimVo {
    return CompanyClaimVo(
        customerCenterPhone,
        zipcode,
        address1,
        address2
    )
}

fun createAccount(
    bankName: String = "기업은행",
    accountNumber: String = "12345678901234",
    ownerName: String = "예금주"
): List<AccountVo> {
    return listOf(AccountVo(
        bankName,
        accountNumber,
        ownerName
    ))
}