package com.jocoos.mybeautip.domain.policy.api.admin

import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest
import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy
import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory
import com.jocoos.mybeautip.domain.policy.persistence.repository.PolicyHistoryRepository
import com.jocoos.mybeautip.domain.policy.persistence.repository.PolicyRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COUNTRY_CODE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeEditPolicy
import com.jocoos.mybeautip.testutil.fixture.makePolicy
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class AdminPolicyHistoryControllerTest(
    private val repository: PolicyHistoryRepository,
    private val policyRepository: PolicyRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun get() {
        val policy = savePolicy()
        val request = makeEditPolicy()
        savePolicyHistory(policy, request)

        val result: ResultActions = mockMvc
            .perform(
                RestDocumentationRequestBuilders.get("/admin/policy/history")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        result.andDo(
            MockMvcRestDocumentation.document(
                "admin_search_policy_history",
                requestParameters(
                    parameterWithName("page").description("페이지").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 카운트"),
                    fieldWithPath("content").type(ARRAY).description("조회 결과"),
                    fieldWithPath("content.[].id").type(NUMBER).description("정책 이력 아이디"),
                    fieldWithPath("content.[].country_code").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("content.[].created_at").type(STRING).description("정책 수정된 시간").attributes(getZonedDateFormat())
                )
            )
        )
    }

    fun savePolicy(): Policy {
        return policyRepository.save(makePolicy())
    }

    fun savePolicyHistory(
        policy: Policy,
        request: EditPolicyRequest
    ): PolicyHistory {
        val policyHistory: PolicyHistory = PolicyHistory(policy, request)
        return repository.save(policyHistory)
    }
}