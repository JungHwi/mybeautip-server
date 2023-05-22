package com.jocoos.mybeautip.domain.delivery.api.admin

import com.jocoos.mybeautip.domain.company.dto.CompanyResponse
import com.jocoos.mybeautip.domain.company.service.CompanyService
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryFeePolicyDao
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCompanyRequest
import com.jocoos.mybeautip.testutil.fixture.makeCreateDeliveryFeePolicyRequest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

class AdminDeliveryFeePolicyControllerTest (
    private val dao: DeliveryFeePolicyDao,
    private val companyService: CompanyService
) : RestDocsIntegrationTestSupport() {

    @Test
    @Transactional
    fun create() {
        val company = saveCompany()
        val request = makeCreateDeliveryFeePolicyRequest(company.id)

        val result: ResultActions = mockMvc
            .perform(
                post("/admin/delivery/fee")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_create_delivery_fee_polish",
                requestFields(
                    fieldWithPath("company_id").type(NUMBER).description("공급사 아이디"),
                    fieldWithPath("name").type(STRING).description("대표 배송비명"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(DELIVERY_FEE_TYPE)),
                    fieldWithPath("payment_option").type(STRING).description(generateLinkCode(PAYMENT_OPTION)),
                    fieldWithPath("status").type(STRING).attributes(getDefault("ACTIVE")).description(generateLinkCode(DELIVERY_FEE_STATUS)),
                    fieldWithPath("delivery_method").type(STRING).attributes(getDefault("COURIER")).description(generateLinkCode(DELIVERY_METHOD)),
                    fieldWithPath("details").type(ARRAY).description("배송비 국가별 상세 정보"),
                    fieldWithPath("details.[].country_code").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("details.[].name").type(STRING).description("국가별 배송비명"),
                    fieldWithPath("details.[].threshold").type(NUMBER).description("국가별 배송비 기준"),
                    fieldWithPath("details.[].fee_below_threshold").type(NUMBER).description("기준 미만일때의 배송비"),
                    fieldWithPath("details.[].fee_above_threshold").type(NUMBER).description("기준 이상일때의 배송비"),
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("배송비 아이디"),
                    fieldWithPath("code").type(STRING).description("배송비 코드"),
                    fieldWithPath("company_id").type(NUMBER).description("공급사 아이디"),
                    fieldWithPath("name").type(STRING).description("대표 배송비명"),
                    fieldWithPath("is_default").type(BOOLEAN).description("기본 배송비 여부"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(DELIVERY_FEE_TYPE)),
                    fieldWithPath("payment_option").type(STRING).description(generateLinkCode(PAYMENT_OPTION)),
                    fieldWithPath("status").type(STRING).attributes(getDefault("ACTIVE")).description(generateLinkCode(DELIVERY_FEE_STATUS)),
                    fieldWithPath("delivery_method").type(STRING).attributes(getDefault("COURIER")).description(generateLinkCode(DELIVERY_METHOD)),
                    fieldWithPath("details").type(ARRAY).description("배송비 국가별 상세 정보"),
                    fieldWithPath("details.[].id").type(NUMBER).description("배송비 국가별 상세 정보 아이디"),
                    fieldWithPath("details.[].country_code").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("details.[].name").type(STRING).description("국가별 배송비명"),
                    fieldWithPath("details.[].threshold").type(NUMBER).description("국가별 배송비 기준"),
                    fieldWithPath("details.[].fee_below_threshold").type(NUMBER).description("기준 미만일때의 배송비"),
                    fieldWithPath("details.[].fee_above_threshold").type(NUMBER).description("기준 이상일때의 배송비"),
                )
            )
        )
    }

    fun saveCompany(): CompanyResponse {
        return companyService.create(makeCompanyRequest())
    }
}