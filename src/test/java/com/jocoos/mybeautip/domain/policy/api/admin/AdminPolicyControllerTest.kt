package com.jocoos.mybeautip.domain.policy.api.admin

import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy
import com.jocoos.mybeautip.domain.policy.persistence.repository.PolicyRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COUNTRY_CODE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeEditPolicy
import com.jocoos.mybeautip.testutil.fixture.makePolicy
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminPolicyControllerTest(
    private val repository: PolicyRepository
) : RestDocsIntegrationTestSupport() {


    @Test
    fun edit() {
        val policy = savePolicy()
        val request = makeEditPolicy()

        val result: ResultActions = mockMvc
            .perform(
                put("/admin/policy/{countryCode}", policy.countryCode)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_policy",
                pathParameters(
                    parameterWithName("countryCode").description(generateLinkCode(COUNTRY_CODE))
                ),
                requestFields(
                    fieldWithPath("delivery_policy").type(STRING).description("배송 정책"),
                    fieldWithPath("claim_policy").type(STRING).description("취소/교환/반품 정책"),
                ),
                responseFields(
                    fieldWithPath("country_code").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("delivery_policy").type(STRING).description("배송 정책"),
                    fieldWithPath("claim_policy").type(STRING).description("취소/교환/반품 정책"),
                )
            )
        )
    }

    fun savePolicy() : Policy {
        return repository.save(makePolicy())
    }
}