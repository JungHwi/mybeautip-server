package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest
import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy
import com.jocoos.mybeautip.global.code.CountryCode

fun makePolicy(
    countryCode: CountryCode? = CountryCode.KR,
    deliveryPolicy: String? = "배송 정책",
    claimPolicy: String? = "클레임 정책"
) : Policy {
    return Policy.builder()
        .countryCode(countryCode)
        .deliveryPolicy(deliveryPolicy)
        .claimPolicy(claimPolicy)
        .build()
}

fun makeEditPolicy(
    deliveryPolicy: String? = "수정된 배송 정책",
    claimPolicy: String? = "수정된 클레임 정책"
) : EditPolicyRequest {
    return EditPolicyRequest.builder()
        .deliveryPolicy(deliveryPolicy)
        .claimPolicy(claimPolicy)
        .build()
}
