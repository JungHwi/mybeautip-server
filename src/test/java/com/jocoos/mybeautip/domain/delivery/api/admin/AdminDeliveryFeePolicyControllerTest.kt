package com.jocoos.mybeautip.domain.delivery.api.admin

import com.jocoos.mybeautip.domain.company.persistence.domain.Company
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryFeePolicyDao
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCompanyRequest
import com.jocoos.mybeautip.testutil.fixture.makeCreateDeliveryFeePolicyRequest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminDeliveryFeePolicyControllerTest (
    private val dao: DeliveryFeePolicyDao,
    private val companyDao: CompanyDao
) : RestDocsIntegrationTestSupport() {

    @Test
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

    @Test
    fun search() {
        saveDeliveryFee()

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/delivery/fee")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_search_delivery_fee_polish",
                requestParameters(
                    parameterWithName("search_field").description(generateLinkCode(DELIVERY_FEE_SEARCH_FIELD)).optional(),
                    parameterWithName("search_text").description("검색어").optional(),
                    parameterWithName("type").description(generateLinkCode(DELIVERY_FEE_TYPE)).optional(),
                    parameterWithName("page").description("페이지").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("조회 총수"),
                    fieldWithPath("content").type(ARRAY).description("조회 결과 데이터"),
                    fieldWithPath("content.[].id").type(NUMBER).description("배송업체 ID"),
                    fieldWithPath("content.[].name").type(STRING).description("배송업체 코드"),
                    fieldWithPath("content.[].company_name").type(STRING).description(generateLinkCode(DELIVERY_COMPANY_STATUS)),
                    fieldWithPath("content.[].type").type(STRING).description("배송업체명"),
                    fieldWithPath("content.[].is_default").type(BOOLEAN).description("배송조회 API URL"),
                    fieldWithPath("content.[].delivery_method").type(STRING).description("배송조회 API URL"),
                    fieldWithPath("content.[].created_at").type(STRING).description("등록일시").attributes(DocumentAttributeGenerator.getZonedDateFormat()),
                    fieldWithPath("content.[].details").type(ARRAY).description("배송비 국가별 상세 정보"),
                    fieldWithPath("content.[].details.[].id").type(NUMBER).description("배송비 상세 정보 아이디"),
                    fieldWithPath("content.[].details.[].country_code").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("content.[].details.[].threshold").type(NUMBER).description("배송비 구분값"),
                    fieldWithPath("content.[].details.[].fee_below_threshold").type(NUMBER).description("구분값 미만 배송비"),
                    fieldWithPath("content.[].details.[].fee_above_threshold").type(NUMBER).description("구분값 이상 배송비"),
                )
            )
        )
    }

    fun saveDeliveryFee(): DeliveryFeePolicy {
        val company = saveCompany()
        return dao.create(company, makeCreateDeliveryFeePolicyRequest(companyId =  company.id))
    }

    fun saveCompany(): Company {
        return companyDao.create(makeCompanyRequest())
    }
}